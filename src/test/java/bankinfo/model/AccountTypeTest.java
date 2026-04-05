package bankinfo.model;

import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.testng.Assert.*;

public class AccountTypeTest {

    @Test
    public void gettersSettersAndToString_shouldWork() {
        AccountType accountType = new AccountType();

        accountType.setName("Savings");
        accountType.setMaxCredit(new BigDecimal("10000.00"));
        accountType.setCreditRepayRule("Monthly repayment");
        accountType.setInterestRate(new BigDecimal("0.0550"));
        accountType.setInterestInterval(InterestInterval.MONTHLY);
        accountType.setInterestMethod(InterestMethod.TO_SAME_ACCOUNT);
        accountType.setAllowDebit(true);
        accountType.setAllowCredit(false);
        accountType.setMinCreditAmount(new BigDecimal("100.00"));
        accountType.setMaxCreditAmount(new BigDecimal("5000.00"));
        accountType.setMinDebitAmount(new BigDecimal("10.00"));
        accountType.setMaxDebitAmount(new BigDecimal("1000.00"));

        assertNull(accountType.getId());
        assertEquals(accountType.getName(), "Savings");
        assertEquals(accountType.getMaxCredit(), new BigDecimal("10000.00"));
        assertEquals(accountType.getCreditRepayRule(), "Monthly repayment");
        assertEquals(accountType.getInterestRate(), new BigDecimal("0.0550"));
        assertEquals(accountType.getInterestInterval(), InterestInterval.MONTHLY);
        assertEquals(accountType.getInterestMethod(), InterestMethod.TO_SAME_ACCOUNT);
        assertTrue(accountType.getAllowDebit());
        assertFalse(accountType.getAllowCredit());
        assertEquals(accountType.getMinCreditAmount(), new BigDecimal("100.00"));
        assertEquals(accountType.getMaxCreditAmount(), new BigDecimal("5000.00"));
        assertEquals(accountType.getMinDebitAmount(), new BigDecimal("10.00"));
        assertEquals(accountType.getMaxDebitAmount(), new BigDecimal("1000.00"));

        String text = accountType.toString();
        assertNotNull(text);
        assertTrue(text.contains("AccountType{"));
        assertTrue(text.contains("id=null"));
        assertTrue(text.contains("name='Savings'"));
        assertTrue(text.contains("maxCredit=10000.00"));
        assertTrue(text.contains("interestRate=0.0550"));
        assertTrue(text.contains("interestInterval=MONTHLY"));
        assertTrue(text.contains("interestMethod=TO_SAME_ACCOUNT"));
        assertTrue(text.contains("allowDebit=true"));
        assertTrue(text.contains("allowCredit=false"));
    }
}