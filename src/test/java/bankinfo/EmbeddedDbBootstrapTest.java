package bankinfo;

import bankinfo.dao.TestDbHelper;
import bankinfo.test.EmbeddedPostgresHolder;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class EmbeddedDbBootstrapTest {

    @BeforeSuite(alwaysRun = true)
    public void startEmbeddedDatabase() {
        EmbeddedPostgresHolder.start();
        TestDbHelper.recreateAndFillDatabase();
    }

    @AfterSuite(alwaysRun = true)
    public void stopEmbeddedDatabase() {
        EmbeddedPostgresHolder.stop();
    }

    @Test
    public void embeddedDatabase_shouldBeStarted() {
        assertTrue(true);
    }
}
