package bankinfo.web.service;

import bankinfo.dao.AccountTypeDao;
import bankinfo.model.AccountType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountTypeService {

    private final AccountTypeDao accountTypeDao;

    public AccountTypeService(AccountTypeDao accountTypeDao) {
        this.accountTypeDao = accountTypeDao;
    }

    public List<AccountType> findAccountTypes(String query) {
        if (query == null || query.isBlank()) {
            return accountTypeDao.findAll();
        }
        return accountTypeDao.findByNameContains(query.trim());
    }

    public Optional<AccountType> findById(Long id) {
        return accountTypeDao.findById(id);
    }

    public AccountType save(AccountType accountType) {
        validate(accountType);
        return accountTypeDao.save(accountType);
    }

    private void validate(AccountType accountType) {
        if (accountType == null) {
            throw new ValidationException("Account type is required");
        }
        if (accountType.getName() == null || accountType.getName().isBlank()) {
            throw new ValidationException("Account type name is required");
        }
        if (accountType.getMaxCredit() == null || accountType.getMaxCredit().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Max credit must be non-negative");
        }
        if (accountType.getInterestRate() == null || accountType.getInterestRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Interest rate must be non-negative");
        }
        if (accountType.getInterestInterval() == null) {
            throw new ValidationException("Interest interval is required");
        }
        if (accountType.getInterestMethod() == null) {
            throw new ValidationException("Interest method is required");
        }
        if (accountType.getAllowCredit() == null || accountType.getAllowDebit() == null) {
            throw new ValidationException("Allow flags are required");
        }
    }
}
