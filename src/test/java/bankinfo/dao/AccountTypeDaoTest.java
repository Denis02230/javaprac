package bankinfo.dao;

import bankinfo.model.AccountType;
import bankinfo.model.InterestInterval;
import bankinfo.model.InterestMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

public class AccountTypeDaoTest {

    private final AccountTypeDao accountTypeDao = new AccountTypeDao();

    @BeforeMethod
    public void resetDatabase() {
        TestDbHelper.recreateAndFillDatabase();
    }

    @Test
    public void findAll_shouldReturnAllAccountTypes() {
        List<AccountType> accountTypes = accountTypeDao.findAll();

        assertNotNull(accountTypes);
        assertEquals(accountTypes.size(), 4);
        assertEquals(accountTypes.get(0).getName(), "Savings Basic");
        assertEquals(accountTypes.get(1).getName(), "Checking Standard");
        assertEquals(accountTypes.get(2).getName(), "Overdraft Account");
        assertEquals(accountTypes.get(3).getName(), "Deposit To Other");
    }

    @Test
    public void findById_shouldReturnAccountType_whenExists() {
        Optional<AccountType> result = accountTypeDao.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(result.get().getName(), "Savings Basic");
        assertEquals(result.get().getInterestRate(), new BigDecimal("0.0500"));
    }

    @Test
    public void findById_shouldReturnEmpty_whenNotExists() {
        Optional<AccountType> result = accountTypeDao.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findByNameContains_shouldReturnMatches_caseInsensitive() {
        List<AccountType> accountTypes = accountTypeDao.findByNameContains("account");

        assertEquals(accountTypes.size(), 1);
        assertEquals(accountTypes.get(0).getName(), "Overdraft Account");
    }

    @Test
    public void findByNameContains_shouldReturnOnlyMatchingRows() {
        List<AccountType> accountTypes = accountTypeDao.findByNameContains("savings");

        assertEquals(accountTypes.size(), 1);
        assertEquals(accountTypes.get(0).getName(), "Savings Basic");
    }

    @Test
    public void findByNameContains_shouldReturnEmpty_whenNoMatches() {
        List<AccountType> accountTypes = accountTypeDao.findByNameContains("zzz");

        assertNotNull(accountTypes);
        assertTrue(accountTypes.isEmpty());
    }

    @Test
    public void findDebitAllowed_shouldReturnOnlyDebitAllowedTypes() {
        List<AccountType> accountTypes = accountTypeDao.findDebitAllowed();

        assertEquals(accountTypes.size(), 3);
        assertTrue(accountTypes.stream().allMatch(AccountType::getAllowDebit));
        assertEquals(accountTypes.get(0).getName(), "Savings Basic");
        assertEquals(accountTypes.get(1).getName(), "Checking Standard");
        assertEquals(accountTypes.get(2).getName(), "Overdraft Account");
    }

    @Test
    public void findCreditAllowed_shouldReturnOnlyCreditAllowedTypes() {
        List<AccountType> accountTypes = accountTypeDao.findCreditAllowed();

        assertEquals(accountTypes.size(), 4);
        assertTrue(accountTypes.stream().allMatch(AccountType::getAllowCredit));
    }

    @Test
    public void findWithInterestRateGreaterThan_shouldReturnMatchingRows() {
        List<AccountType> accountTypes =
                accountTypeDao.findWithInterestRateGreaterThan(new BigDecimal("0.00"));

        assertEquals(accountTypes.size(), 2);
        assertEquals(accountTypes.get(0).getName(), "Savings Basic");
        assertEquals(accountTypes.get(1).getName(), "Deposit To Other");
        assertTrue(accountTypes.stream().allMatch(at -> at.getInterestRate().compareTo(BigDecimal.ZERO) > 0));
    }

    @Test
    public void findWithInterestRateGreaterThan_shouldReturnEmpty_whenNoMatches() {
        List<AccountType> accountTypes =
                accountTypeDao.findWithInterestRateGreaterThan(new BigDecimal("100.00"));

        assertNotNull(accountTypes);
        assertTrue(accountTypes.isEmpty());
    }

    @Test
    public void save_shouldInsertNewAccountType() {
        AccountType accountType = new AccountType();
        accountType.setName("Premium Custom");
        accountType.setMaxCredit(new BigDecimal("15000.00"));
        accountType.setCreditRepayRule("Repay within 45 days.");
        accountType.setInterestRate(new BigDecimal("0.0150"));
        accountType.setInterestInterval(InterestInterval.MONTHLY);
        accountType.setInterestMethod(InterestMethod.TO_SAME_ACCOUNT);
        accountType.setAllowDebit(true);
        accountType.setAllowCredit(true);
        accountType.setMinCreditAmount(new BigDecimal("100.00"));
        accountType.setMaxCreditAmount(new BigDecimal("300000.00"));
        accountType.setMinDebitAmount(new BigDecimal("50.00"));
        accountType.setMaxDebitAmount(new BigDecimal("150000.00"));

        AccountType saved = accountTypeDao.save(accountType);

        assertNotNull(saved.getId());

        Optional<AccountType> loaded = accountTypeDao.findById(saved.getId());
        assertTrue(loaded.isPresent());
        assertEquals(loaded.get().getName(), "Premium Custom");
        assertEquals(loaded.get().getMaxCredit(), new BigDecimal("15000.00"));
        assertEquals(loaded.get().getCreditRepayRule(), "Repay within 45 days.");
        assertEquals(loaded.get().getInterestRate(), new BigDecimal("0.0150"));
        assertEquals(loaded.get().getInterestInterval(), InterestInterval.MONTHLY);
        assertEquals(loaded.get().getInterestMethod(), InterestMethod.TO_SAME_ACCOUNT);
        assertTrue(loaded.get().getAllowDebit());
        assertTrue(loaded.get().getAllowCredit());
        assertEquals(loaded.get().getMinCreditAmount(), new BigDecimal("100.00"));
        assertEquals(loaded.get().getMaxCreditAmount(), new BigDecimal("300000.00"));
        assertEquals(loaded.get().getMinDebitAmount(), new BigDecimal("50.00"));
        assertEquals(loaded.get().getMaxDebitAmount(), new BigDecimal("150000.00"));
    }
}