package bankinfo.dao;

import bankinfo.model.Account;
import bankinfo.model.AccountTx;
import bankinfo.model.TxType;
import bankinfo.util.HibernateUtil;
import org.hibernate.Session;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

public class AccountTxDaoTest {

    private final AccountTxDao accountTxDao = new AccountTxDao();

    @BeforeMethod
    public void resetDatabase() {
        TestDbHelper.recreateAndFillDatabase();
    }

    @Test
    public void findAll_shouldReturnAllTransactions() {
        List<AccountTx> txs = accountTxDao.findAll();

        assertNotNull(txs);
        assertEquals(txs.size(), 26);
        assertEquals(txs.get(0).getId(), Long.valueOf(1L));
        assertEquals(txs.get(25).getId(), Long.valueOf(26L));
    }

    @Test
    public void findById_shouldReturnTransaction_whenExists() {
        Optional<AccountTx> result = accountTxDao.findById(10L);

        assertTrue(result.isPresent());
        assertEquals(result.get().getTxType(), TxType.CREDIT);
        assertEquals(result.get().getAmount(), new BigDecimal("20000.00"));
    }

    @Test
    public void findById_shouldReturnEmpty_whenMissing() {
        Optional<AccountTx> result = accountTxDao.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findByIdDetailed_shouldLoadLinkedEntities() {
        Optional<AccountTx> result = accountTxDao.findByIdDetailed(10L);

        assertTrue(result.isPresent());

        AccountTx tx = result.get();
        assertEquals(tx.getId(), Long.valueOf(10L));
        assertEquals(tx.getAccount().getAccountNumber(), "ACC-0004");
        assertEquals(tx.getAccount().getClient().getDisplayName(), "Alpha LLC");
        assertEquals(tx.getAccount().getBranch().getName(), "Central Branch");
        assertEquals(tx.getAccount().getAccountType().getName(), "Checking Standard");
    }

    @Test
    public void findByAccountId_shouldReturnOnlyAccountTransactions() {
        List<AccountTx> txs = accountTxDao.findByAccountId(4L);

        assertEquals(txs.size(), 6);
        assertTrue(txs.stream().allMatch(tx -> tx.getAccount().getId().equals(4L)));
        assertEquals(txs.get(0).getId(), Long.valueOf(10L));
        assertEquals(txs.get(1).getId(), Long.valueOf(13L));
        assertEquals(txs.get(2).getId(), Long.valueOf(14L));
        assertEquals(txs.get(3).getId(), Long.valueOf(15L));
        assertEquals(txs.get(4).getId(), Long.valueOf(11L));
        assertEquals(txs.get(5).getId(), Long.valueOf(12L));
    }

    @Test
    public void findByAccountId_shouldReturnEmpty_whenNoMatches() {
        List<AccountTx> txs = accountTxDao.findByAccountId(999L);

        assertNotNull(txs);
        assertTrue(txs.isEmpty());
    }

    @Test
    public void findByType_shouldReturnOnlyCreditTransactions() {
        List<AccountTx> txs = accountTxDao.findByType(TxType.CREDIT);

        assertEquals(txs.size(), 10);
        assertTrue(txs.stream().allMatch(tx -> tx.getTxType() == TxType.CREDIT));
    }

    @Test
    public void findByType_shouldReturnOnlyDebitTransactions() {
        List<AccountTx> txs = accountTxDao.findByType(TxType.DEBIT);

        assertEquals(txs.size(), 11);
        assertTrue(txs.stream().allMatch(tx -> tx.getTxType() == TxType.DEBIT));
    }

    @Test
    public void findByType_shouldReturnOnlyInterestTransactions() {
        List<AccountTx> txs = accountTxDao.findByType(TxType.INTEREST);

        assertEquals(txs.size(), 5);
        assertTrue(txs.stream().allMatch(tx -> tx.getTxType() == TxType.INTEREST));
    }

    @Test
    public void findByAccountIdAndType_shouldReturnMatchingRows() {
        List<AccountTx> txs = accountTxDao.findByAccountIdAndType(4L, TxType.INTEREST);

        assertEquals(txs.size(), 3);
        assertTrue(txs.stream().allMatch(tx -> tx.getAccount().getId().equals(4L)));
        assertTrue(txs.stream().allMatch(tx -> tx.getTxType() == TxType.INTEREST));
        assertEquals(txs.get(0).getId(), Long.valueOf(13L));
        assertEquals(txs.get(1).getId(), Long.valueOf(14L));
        assertEquals(txs.get(2).getId(), Long.valueOf(15L));
    }

    @Test
    public void findByAccountIdAndType_shouldReturnEmpty_whenNoMatches() {
        List<AccountTx> txs = accountTxDao.findByAccountIdAndType(5L, TxType.DEBIT);

        assertNotNull(txs);
        assertTrue(txs.isEmpty());
    }

    @Test
    public void findByPeriod_shouldReturnMatchingRows() {
        List<AccountTx> txs = accountTxDao.findByPeriod(
                OffsetDateTime.parse("2026-02-06T00:00:00+00:00"),
                OffsetDateTime.parse("2026-02-06T23:59:59+00:00")
        );

        assertEquals(txs.size(), 3);
        assertEquals(txs.get(0).getId(), Long.valueOf(4L));
        assertEquals(txs.get(1).getId(), Long.valueOf(13L));
        assertEquals(txs.get(2).getId(), Long.valueOf(8L));
    }

    @Test
    public void findByPeriod_shouldReturnEmpty_whenOutsideRange() {
        List<AccountTx> txs = accountTxDao.findByPeriod(
                OffsetDateTime.parse("2027-01-01T00:00:00+00:00"),
                OffsetDateTime.parse("2027-12-31T23:59:59+00:00")
        );

        assertNotNull(txs);
        assertTrue(txs.isEmpty());
    }

    @Test
    public void findWithAmountGreaterThan_shouldReturnMatchingRows() {
        List<AccountTx> txs = accountTxDao.findWithAmountGreaterThan(new BigDecimal("10000.00"));

        assertEquals(txs.size(), 3);
        assertEquals(txs.get(0).getId(), Long.valueOf(10L));
        assertEquals(txs.get(1).getId(), Long.valueOf(16L));
        assertEquals(txs.get(2).getId(), Long.valueOf(17L));
    }

    @Test
    public void findWithAmountGreaterThan_shouldReturnEmpty_whenNoMatches() {
        List<AccountTx> txs = accountTxDao.findWithAmountGreaterThan(new BigDecimal("1000000.00"));

        assertNotNull(txs);
        assertTrue(txs.isEmpty());
    }

    @Test
    public void save_shouldInsertNewTransaction() {
        AccountTx tx = new AccountTx();
        tx.setTxType(TxType.CREDIT);
        tx.setAmount(new BigDecimal("777.77"));
        tx.setTxTime(OffsetDateTime.parse("2026-03-10T10:15:00+03:00"));
        tx.setDescription("Test transaction");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Account account = session.get(Account.class, 1L);
            tx.setAccount(account);
        }

        AccountTx saved = accountTxDao.save(tx);

        assertNotNull(saved.getId());

        Optional<AccountTx> loaded = accountTxDao.findByIdDetailed(saved.getId());
        assertTrue(loaded.isPresent());
        assertEquals(loaded.get().getTxType(), TxType.CREDIT);
        assertEquals(loaded.get().getAmount(), new BigDecimal("777.77"));
        assertEquals(loaded.get().getDescription(), "Test transaction");
        assertEquals(loaded.get().getAccount().getId(), Long.valueOf(1L));
    }

    @Test
    public void findByIdDetailed_shouldReturnEmpty_whenMissing() {
        Optional<AccountTx> result = accountTxDao.findByIdDetailed(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void save_shouldUpdateExistingTransaction() {
        Optional<AccountTx> loadedBefore = accountTxDao.findById(1L);
        assertTrue(loadedBefore.isPresent());

        AccountTx tx = loadedBefore.get();
        tx.setTxType(TxType.DEBIT);
        tx.setAmount(new BigDecimal("1234.56"));
        tx.setTxTime(OffsetDateTime.parse("2026-03-11T09:30:00+03:00"));
        tx.setDescription("Updated transaction");

        AccountTx saved = accountTxDao.save(tx);

        assertNotNull(saved);
        assertEquals(saved.getId(), Long.valueOf(1L));

        Optional<AccountTx> loadedAfter = accountTxDao.findById(1L);
        assertTrue(loadedAfter.isPresent());
        assertEquals(loadedAfter.get().getId(), Long.valueOf(1L));
        assertEquals(loadedAfter.get().getTxType(), TxType.DEBIT);
        assertEquals(loadedAfter.get().getAmount(), new BigDecimal("1234.56"));
        assertEquals(
                loadedAfter.get().getTxTime(),
                OffsetDateTime.parse("2026-03-11T09:30:00+03:00")
        );
        assertEquals(loadedAfter.get().getDescription(), "Updated transaction");
    }
}