package bankinfo.util;

import org.hibernate.SessionFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class HibernateUtilTest {

    @Test
    public void shutdown_shouldBeSafe_whenCalledTwice() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

        assertNotNull(sessionFactory);
        assertFalse(sessionFactory.isClosed());

        HibernateUtil.shutdown();
        assertTrue(sessionFactory.isClosed());

        HibernateUtil.shutdown();
        assertTrue(sessionFactory.isClosed());
    }
}