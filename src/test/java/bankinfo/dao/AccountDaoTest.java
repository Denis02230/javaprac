package bankinfo.dao;

import bankinfo.model.Account;
import bankinfo.model.AccountStatus;
import bankinfo.model.AccountTx;
import bankinfo.util.HibernateUtil;
import org.hibernate.Session;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

public class AccountDaoTest {

    private final AccountDao accountDao = new AccountDao();

    @BeforeMethod
    public void resetDatabase() {
        TestDbHelper.recreateAndFillDatabase();
    }

    @Test
    public void findAll_shouldReturnAllAccounts() {
        List<Account> accounts = accountDao.findAll();

        assertNotNull(accounts);
        assertEquals(accounts.size(), 8);
        assertEquals(accounts.get(0).getAccountNumber(), "ACC-0001");
        assertEquals(accounts.get(7).getAccountNumber(), "ACC-0008");
    }

    @Test
    public void findById_shouldReturnAccount_whenExists() {
        Optional<Account> result = accountDao.findById(5L);

        assertTrue(result.isPresent());
        assertEquals(result.get().getAccountNumber(), "ACC-0005");
        assertEquals(result.get().getStatus(), AccountStatus.OPEN);
    }

    @Test
    public void findById_shouldReturnEmpty_whenMissing() {
        Optional<Account> result = accountDao.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findByIdDetailed_shouldLoadLinkedEntities() {
        Optional<Account> result = accountDao.findByIdDetailed(5L);

        assertTrue(result.isPresent());

        Account account = result.get();
        assertEquals(account.getAccountNumber(), "ACC-0005");
        assertEquals(account.getClient().getDisplayName(), "Alpha LLC");
        assertEquals(account.getBranch().getName(), "Central Branch");
        assertEquals(account.getAccountType().getName(), "Deposit To Other");
        assertNotNull(account.getInterestTargetAccount());
        assertEquals(account.getInterestTargetAccount().getAccountNumber(), "ACC-0004");
    }

    @Test
    public void findByIdWithTransactions_shouldLoadTransactions() {
        Optional<Account> result = accountDao.findByIdWithTransactions(1L);

        assertTrue(result.isPresent());
        Account account = result.get();

        assertEquals(account.getAccountNumber(), "ACC-0001");
        assertEquals(account.getTransactions().size(), 3);
        assertEquals(account.getTransactions().get(0).getId(), Long.valueOf(1L));
        assertEquals(account.getTransactions().get(1).getId(), Long.valueOf(2L));
        assertEquals(account.getTransactions().get(2).getId(), Long.valueOf(3L));
    }

    @Test
    public void findByIdWithTransactions_shouldReturnEmpty_whenMissing() {
        Optional<Account> result = accountDao.findByIdWithTransactions(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findByClientId_shouldReturnOnlyClientAccounts() {
        List<Account> accounts = accountDao.findByClientId(4L);

        assertEquals(accounts.size(), 2);
        assertEquals(accounts.get(0).getAccountNumber(), "ACC-0004");
        assertEquals(accounts.get(1).getAccountNumber(), "ACC-0005");
    }

    @Test
    public void findByClientId_shouldReturnEmpty_whenClientHasNoAccounts() {
        List<Account> accounts = accountDao.findByClientId(999L);

        assertNotNull(accounts);
        assertTrue(accounts.isEmpty());
    }

    @Test
    public void findByBranchId_shouldReturnOnlyBranchAccounts() {
        List<Account> accounts = accountDao.findByBranchId(1L);

        assertEquals(accounts.size(), 4);
        assertEquals(accounts.get(0).getAccountNumber(), "ACC-0001");
        assertEquals(accounts.get(1).getAccountNumber(), "ACC-0002");
        assertEquals(accounts.get(2).getAccountNumber(), "ACC-0004");
        assertEquals(accounts.get(3).getAccountNumber(), "ACC-0005");
    }

    @Test
    public void findByBranchId_shouldReturnEmpty_whenNoAccounts() {
        List<Account> accounts = accountDao.findByBranchId(999L);

        assertNotNull(accounts);
        assertTrue(accounts.isEmpty());
    }

    @Test
    public void findByAccountTypeId_shouldReturnOnlyMatchingAccounts() {
        List<Account> accounts = accountDao.findByAccountTypeId(2L);

        assertEquals(accounts.size(), 4);
        assertEquals(accounts.get(0).getAccountNumber(), "ACC-0001");
        assertEquals(accounts.get(1).getAccountNumber(), "ACC-0004");
        assertEquals(accounts.get(2).getAccountNumber(), "ACC-0006");
        assertEquals(accounts.get(3).getAccountNumber(), "ACC-0008");
    }

    @Test
    public void findByAccountTypeId_shouldReturnEmpty_whenNoMatches() {
        List<Account> accounts = accountDao.findByAccountTypeId(999L);

        assertNotNull(accounts);
        assertTrue(accounts.isEmpty());
    }

    @Test
    public void findByStatus_shouldReturnOnlyOpenAccounts() {
        List<Account> accounts = accountDao.findByStatus(AccountStatus.OPEN);

        assertEquals(accounts.size(), 6);
        assertTrue(accounts.stream().allMatch(a -> a.getStatus() == AccountStatus.OPEN));
    }

    @Test
    public void findByStatus_shouldReturnOnlyClosedAccounts() {
        List<Account> accounts = accountDao.findByStatus(AccountStatus.CLOSED);

        assertEquals(accounts.size(), 2);
        assertTrue(accounts.stream().allMatch(a -> a.getStatus() == AccountStatus.CLOSED));
    }

    @Test
    public void findOpenAccounts_shouldReturnOnlyAccountsWithoutClosedAt() {
        List<Account> accounts = accountDao.findOpenAccounts();

        assertEquals(accounts.size(), 6);
        assertTrue(accounts.stream().allMatch(a -> a.getClosedAt() == null));
    }

    @Test
    public void findWithBalanceGreaterThan_shouldReturnMatchingAccounts() {
        List<Account> accounts = accountDao.findWithBalanceGreaterThan(new BigDecimal("10000.00"));

        assertEquals(accounts.size(), 3);
        assertEquals(accounts.get(0).getAccountNumber(), "ACC-0004");
        assertEquals(accounts.get(1).getAccountNumber(), "ACC-0005");
        assertEquals(accounts.get(2).getAccountNumber(), "ACC-0006");
    }

    @Test
    public void findWithBalanceGreaterThan_shouldReturnEmpty_whenNoMatches() {
        List<Account> accounts = accountDao.findWithBalanceGreaterThan(new BigDecimal("1000000.00"));

        assertNotNull(accounts);
        assertTrue(accounts.isEmpty());
    }

    @Test
    public void findTransactionsByAccountId_shouldReturnTransactions() {
        List<AccountTx> txs = accountDao.findTransactionsByAccountId(1L);

        assertNotNull(txs);
        assertEquals(txs.size(), 3);
        assertTrue(txs.stream().allMatch(tx -> tx.getAccount().getId().equals(1L)));
        assertEquals(txs.get(0).getId(), Long.valueOf(1L));
        assertEquals(txs.get(1).getId(), Long.valueOf(2L));
        assertEquals(txs.get(2).getId(), Long.valueOf(3L));
    }

    @Test
    public void findTransactionsByAccountId_shouldReturnEmpty_whenNoTransactions() {
        List<AccountTx> txs = accountDao.findTransactionsByAccountId(999L);

        assertNotNull(txs);
        assertTrue(txs.isEmpty());
    }

    @Test
    public void findTransactionsByAccountIdAndPeriod_shouldReturnMatchingRows() {
        List<AccountTx> txs = accountDao.findTransactionsByAccountIdAndPeriod(
                1L,
                OffsetDateTime.parse("2026-02-05T00:00:00+00:00"),
                OffsetDateTime.parse("2026-02-10T23:59:59+00:00")
        );

        assertEquals(txs.size(), 2);
        assertTrue(txs.stream().allMatch(tx -> tx.getAccount().getId().equals(1L)));
        assertEquals(txs.get(0).getId(), Long.valueOf(1L));
        assertEquals(txs.get(1).getId(), Long.valueOf(2L));
    }

    @Test
    public void findTransactionsByAccountIdAndPeriod_shouldReturnEmpty_whenOutsidePeriod() {
        List<AccountTx> txs = accountDao.findTransactionsByAccountIdAndPeriod(
                1L,
                OffsetDateTime.parse("2027-01-01T00:00:00+03:00"),
                OffsetDateTime.parse("2027-12-31T23:59:59+03:00")
        );

        assertNotNull(txs);
        assertTrue(txs.isEmpty());
    }

    @Test
    public void save_shouldInsertNewAccount() {
        Account account = new Account();
        account.setAccountNumber("ACC-TEST-1001");
        account.setStatus(AccountStatus.OPEN);
        account.setOpenedAt(OffsetDateTime.parse("2026-03-05T10:00:00+03:00"));
        account.setClosedAt(null);
        account.setBalance(new BigDecimal("2500.00"));

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            account.setClient(session.get(bankinfo.model.Client.class, 1L));
            account.setBranch(session.get(bankinfo.model.Branch.class, 2L));
            account.setAccountType(session.get(bankinfo.model.AccountType.class, 2L));
            account.setInterestTargetAccount(null);
        }

        Account saved = accountDao.save(account);

        assertNotNull(saved.getId());

        Optional<Account> loaded = accountDao.findByIdDetailed(saved.getId());
        assertTrue(loaded.isPresent());
        assertEquals(loaded.get().getAccountNumber(), "ACC-TEST-1001");
        assertEquals(loaded.get().getClient().getId(), Long.valueOf(1L));
        assertEquals(loaded.get().getBranch().getId(), Long.valueOf(2L));
        assertEquals(loaded.get().getAccountType().getId(), Long.valueOf(2L));
        assertEquals(loaded.get().getBalance(), new BigDecimal("2500.00"));
    }
}