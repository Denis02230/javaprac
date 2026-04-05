package bankinfo.model;

import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.testng.Assert.*;

public class AccountTest {

    @Test
    public void gettersSettersAndToString_shouldWork() {
        Account account = new Account();

        Client client = new Client(
                ClientType.PERSON,
                "Ivan Petrov",
                OffsetDateTime.parse("2026-03-01T10:00:00+03:00")
        );

        Branch branch = new Branch("Main Branch", "Old Address, 1");

        AccountType accountType = new AccountType();
        accountType.setName("Savings");
        accountType.setMaxCredit(new BigDecimal("10000.00"));
        accountType.setCreditRepayRule("Monthly");
        accountType.setInterestRate(new BigDecimal("0.0500"));
        accountType.setAllowDebit(true);
        accountType.setAllowCredit(false);

        OffsetDateTime openedAt = OffsetDateTime.parse("2026-03-02T09:15:00+03:00");
        OffsetDateTime closedAt = OffsetDateTime.parse("2026-03-10T18:45:00+03:00");

        account.setAccountNumber("ACC-001");
        account.setClient(client);
        account.setBranch(branch);
        account.setAccountType(accountType);
        account.setStatus(AccountStatus.OPEN);
        account.setOpenedAt(openedAt);
        account.setClosedAt(closedAt);
        account.setBalance(new BigDecimal("1234.56"));

        assertNull(account.getId());
        assertEquals(account.getAccountNumber(), "ACC-001");
        assertSame(account.getClient(), client);
        assertSame(account.getBranch(), branch);
        assertSame(account.getAccountType(), accountType);
        assertEquals(account.getStatus(), AccountStatus.OPEN);
        assertEquals(account.getOpenedAt(), openedAt);
        assertEquals(account.getClosedAt(), closedAt);
        assertEquals(account.getBalance(), new BigDecimal("1234.56"));

        assertNull(account.getInterestTargetAccount());

        assertNotNull(account.getTransactions());
        assertTrue(account.getTransactions().isEmpty());

        String text = account.toString();
        assertNotNull(text);
        assertTrue(text.contains("Account{"));
        assertTrue(text.contains("id=null"));
        assertTrue(text.contains("accountNumber='ACC-001'"));
        assertTrue(text.contains("status=OPEN"));
        assertTrue(text.contains("openedAt=2026-03-02T09:15+03:00"));
        assertTrue(text.contains("closedAt=2026-03-10T18:45+03:00"));
        assertTrue(text.contains("balance=1234.56"));
    }
}