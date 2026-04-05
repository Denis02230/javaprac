package bankinfo.dao;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class DaoExceptionTest {

    @Test
    public void constructorWithMessageAndCause_shouldStoreBoth() {
        RuntimeException cause = new RuntimeException("root cause");

        DaoException ex = new DaoException("dao failed", cause);

        assertEquals(ex.getMessage(), "dao failed");
        assertSame(ex.getCause(), cause);
    }

    @Test
    public void constructorWithMessageOnly_shouldStoreMessage() {
        DaoException ex = new DaoException("dao failed");

        assertEquals(ex.getMessage(), "dao failed");
        assertNull(ex.getCause());
    }
}