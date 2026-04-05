package bankinfo.model;

import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.testng.Assert.*;

public class AccountTxTest {

    @Test
    public void gettersSettersAndToString_shouldWork() {
        AccountTx tx = new AccountTx();

        Account account = new Account();
        account.setAccountNumber("ACC-001");

        OffsetDateTime txTime = OffsetDateTime.parse("2026-03-05T14:20:00+03:00");

        tx.setAccount(account);
        tx.setTxType(TxType.INTEREST);
        tx.setAmount(new BigDecimal("45.67"));
        tx.setTxTime(txTime);
        tx.setDescription("Monthly interest");

        assertNull(tx.getId());
        assertSame(tx.getAccount(), account);
        assertEquals(tx.getTxType(), TxType.INTEREST);
        assertEquals(tx.getAmount(), new BigDecimal("45.67"));
        assertEquals(tx.getTxTime(), txTime);
        assertEquals(tx.getDescription(), "Monthly interest");

        String text = tx.toString();
        assertNotNull(text);
        assertTrue(text.contains("AccountTx{"));
        assertTrue(text.contains("id=null"));
        assertTrue(text.contains("txType=INTEREST"));
        assertTrue(text.contains("amount=45.67"));
        assertTrue(text.contains("txTime=2026-03-05T14:20+03:00"));
        assertTrue(text.contains("description='Monthly interest'"));
    }
}