package bankinfo.web.service;

import bankinfo.dao.AccountDao;
import bankinfo.dao.AccountTxDao;
import bankinfo.dao.AccountTypeDao;
import bankinfo.dao.BranchDao;
import bankinfo.dao.ClientDao;
import bankinfo.model.Account;
import bankinfo.model.AccountStatus;
import bankinfo.model.AccountTx;
import bankinfo.model.AccountType;
import bankinfo.model.InterestInterval;
import bankinfo.model.InterestMethod;
import bankinfo.model.TxType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountDao accountDao;
    private final AccountTxDao accountTxDao;
    private final AccountTypeDao accountTypeDao;
    private final BranchDao branchDao;
    private final ClientDao clientDao;

    public AccountService(
            AccountDao accountDao,
            AccountTxDao accountTxDao,
            AccountTypeDao accountTypeDao,
            BranchDao branchDao,
            ClientDao clientDao
    ) {
        this.accountDao = accountDao;
        this.accountTxDao = accountTxDao;
        this.accountTypeDao = accountTypeDao;
        this.branchDao = branchDao;
        this.clientDao = clientDao;
    }

    public List<Account> findAccounts(
            AccountStatus status,
            Long clientId,
            Long branchId,
            Long accountTypeId,
            String accountNumber
    ) {
        List<Account> accounts = accountDao.findAllDetailed();
        List<Account> filtered = new ArrayList<>();

        String normalizedAccountNumber = accountNumber == null ? null : accountNumber.trim().toLowerCase();

        for (Account account : accounts) {
            if (status != null && account.getStatus() != status) {
                continue;
            }
            if (clientId != null && !account.getClient().getId().equals(clientId)) {
                continue;
            }
            if (branchId != null && !account.getBranch().getId().equals(branchId)) {
                continue;
            }
            if (accountTypeId != null && !account.getAccountType().getId().equals(accountTypeId)) {
                continue;
            }
            if (normalizedAccountNumber != null
                    && !normalizedAccountNumber.isBlank()
                    && !account.getAccountNumber().toLowerCase().contains(normalizedAccountNumber)) {
                continue;
            }
            filtered.add(account);
        }

        return filtered;
    }

    public Optional<Account> findAccountDetailed(Long accountId) {
        return accountDao.findByIdDetailed(accountId);
    }

    public List<AccountTx> findTransactions(Long accountId, OffsetDateTime fromInclusive, OffsetDateTime toInclusive) {
        if (fromInclusive != null && toInclusive != null) {
            return accountDao.findTransactionsByAccountIdAndPeriod(accountId, fromInclusive, toInclusive);
        }
        return accountDao.findTransactionsByAccountId(accountId);
    }

    public Account openAccount(Long clientId, Long branchId, Long accountTypeId) {
        if (clientId == null) {
            throw new ValidationException("Client is required");
        }
        if (branchId == null) {
            throw new ValidationException("Branch is required");
        }
        if (accountTypeId == null) {
            throw new ValidationException("Account type is required");
        }

        Account account = new Account();
        account.setClient(clientDao.findById(clientId)
                .orElseThrow(() -> new ValidationException("Client not found: " + clientId)));
        account.setBranch(branchDao.findById(branchId)
                .orElseThrow(() -> new ValidationException("Branch not found: " + branchId)));
        account.setAccountType(accountTypeDao.findById(accountTypeId)
                .orElseThrow(() -> new ValidationException("Account type not found: " + accountTypeId)));

        account.setStatus(AccountStatus.OPEN);
        account.setOpenedAt(OffsetDateTime.now());
        account.setClosedAt(null);
        account.setBalance(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        account.setAccountNumber("ACC-TMP-" + System.nanoTime());

        Account saved = accountDao.save(account);
        saved.setAccountNumber(String.format("ACC-%04d", saved.getId()));
        return accountDao.save(saved);
    }

    public Account closeAccount(Long accountId) {
        Account account = accountDao.findByIdDetailed(accountId)
                .orElseThrow(() -> new ValidationException("Account not found: " + accountId));

        if (account.getStatus() == AccountStatus.CLOSED || account.getClosedAt() != null) {
            throw new ValidationException("Account is already closed");
        }

        if (account.getBalance().compareTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)) != 0) {
            throw new ValidationException("Account can be closed only with zero balance");
        }

        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(OffsetDateTime.now());
        return accountDao.save(account);
    }

    public AccountTx postTransaction(Long accountId, TxType txType, BigDecimal amount, String description) {
        if (txType == null) {
            throw new ValidationException("Transaction type is required");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount must be greater than zero");
        }

        Account account = accountDao.findByIdDetailed(accountId)
                .orElseThrow(() -> new ValidationException("Account not found: " + accountId));

        if (account.getStatus() != AccountStatus.OPEN || account.getClosedAt() != null) {
            throw new ValidationException("Transaction is not allowed for closed account");
        }

        validateTransactionByRules(account, txType, amount);

        BigDecimal newBalance = account.getBalance();
        if (txType == TxType.DEBIT) {
            newBalance = newBalance.subtract(amount);
        } else {
            newBalance = newBalance.add(amount);
        }

        if (txType == TxType.DEBIT) {
            BigDecimal maxCredit = account.getAccountType().getMaxCredit();
            if (maxCredit == null) {
                maxCredit = BigDecimal.ZERO;
            }
            BigDecimal minAllowed = maxCredit.negate();
            if (newBalance.compareTo(minAllowed) < 0) {
                throw new ValidationException("Debit would exceed max credit limit");
            }
        }

        account.setBalance(newBalance.setScale(2, RoundingMode.HALF_UP));
        accountDao.save(account);

        AccountTx tx = new AccountTx();
        tx.setAccount(account);
        tx.setTxType(txType);
        tx.setAmount(amount.setScale(2, RoundingMode.HALF_UP));
        tx.setTxTime(OffsetDateTime.now());
        tx.setDescription(description == null ? null : description.trim());

        return accountTxDao.save(tx);
    }

    public InterestRunResult runInterest(OffsetDateTime runAt) {
        OffsetDateTime txTime = runAt == null ? OffsetDateTime.now() : runAt;
        InterestRunResult result = new InterestRunResult();

        List<Account> accounts = accountDao.findAllDetailed();
        for (Account account : accounts) {
            if (account.getStatus() != AccountStatus.OPEN || account.getClosedAt() != null) {
                result.addSkippedAccount(account.getAccountNumber() + ": closed");
                continue;
            }

            AccountType accountType = account.getAccountType();
            if (accountType.getInterestRate() == null || accountType.getInterestRate().compareTo(BigDecimal.ZERO) <= 0) {
                result.addSkippedAccount(account.getAccountNumber() + ": zero interest rate");
                continue;
            }
            if (account.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
                result.addSkippedAccount(account.getAccountNumber() + ": non-positive balance");
                continue;
            }

            BigDecimal interestAmount = calculateInterest(account.getBalance(), accountType.getInterestRate(), accountType.getInterestInterval());
            if (interestAmount.compareTo(BigDecimal.ZERO) <= 0) {
                result.addSkippedAccount(account.getAccountNumber() + ": calculated interest is zero");
                continue;
            }

            Account targetAccount = resolveInterestTarget(account);
            targetAccount.setBalance(targetAccount.getBalance().add(interestAmount));
            accountDao.save(targetAccount);

            AccountTx interestTx = new AccountTx();
            interestTx.setAccount(targetAccount);
            interestTx.setTxType(TxType.INTEREST);
            interestTx.setAmount(interestAmount);
            interestTx.setTxTime(txTime);
            interestTx.setDescription("Auto interest from " + account.getAccountNumber());
            accountTxDao.save(interestTx);

            result.incrementCreatedOperations();
            result.addProcessedAccount(account.getAccountNumber() + " -> " + targetAccount.getAccountNumber());
            result.addTotalInterest(interestAmount);
        }

        return result;
    }

    private Account resolveInterestTarget(Account sourceAccount) {
        if (sourceAccount.getAccountType().getInterestMethod() == InterestMethod.TO_OTHER_ACCOUNT
                && sourceAccount.getInterestTargetAccount() != null) {
            Long targetId = sourceAccount.getInterestTargetAccount().getId();
            return accountDao.findByIdDetailed(targetId)
                    .orElseThrow(() -> new ValidationException("Interest target account not found: " + targetId));
        }
        return sourceAccount;
    }

    private BigDecimal calculateInterest(BigDecimal balance, BigDecimal yearlyRate, InterestInterval interval) {
        BigDecimal divisor;
        if (interval == InterestInterval.DAILY) {
            divisor = new BigDecimal("365");
        } else if (interval == InterestInterval.MONTHLY) {
            divisor = new BigDecimal("12");
        } else if (interval == InterestInterval.QUARTERLY) {
            divisor = new BigDecimal("4");
        } else {
            divisor = BigDecimal.ONE;
        }

        return balance
                .multiply(yearlyRate)
                .divide(divisor, 2, RoundingMode.HALF_UP);
    }

    private void validateTransactionByRules(Account account, TxType txType, BigDecimal amount) {
        AccountType type = account.getAccountType();

        if (txType == TxType.CREDIT) {
            if (!Boolean.TRUE.equals(type.getAllowCredit())) {
                throw new ValidationException("Credit operation is not allowed for account type " + type.getName());
            }
            validateRange(amount, type.getMinCreditAmount(), type.getMaxCreditAmount(), "Credit amount");
            return;
        }

        if (txType == TxType.DEBIT) {
            if (!Boolean.TRUE.equals(type.getAllowDebit())) {
                throw new ValidationException("Debit operation is not allowed for account type " + type.getName());
            }
            validateRange(amount, type.getMinDebitAmount(), type.getMaxDebitAmount(), "Debit amount");
            return;
        }

        if (txType == TxType.INTEREST) {
            return;
        }

        throw new ValidationException("Unsupported transaction type: " + txType);
    }

    private void validateRange(BigDecimal amount, BigDecimal minValue, BigDecimal maxValue, String label) {
        if (minValue != null && amount.compareTo(minValue) < 0) {
            throw new ValidationException(label + " must be >= " + minValue);
        }
        if (maxValue != null && amount.compareTo(maxValue) > 0) {
            throw new ValidationException(label + " must be <= " + maxValue);
        }
    }
}
