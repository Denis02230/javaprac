package bankinfo.test;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public final class EmbeddedPostgresHolder {

    private static final String HOST = "127.0.0.1";
    private static final String DB_NAME = "bankinfo";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "";

    private static EmbeddedPostgres embeddedPostgres;

    private EmbeddedPostgresHolder() {
    }

    public static synchronized void start() {
        if (embeddedPostgres != null) {
            return;
        }

        try {
            embeddedPostgres = EmbeddedPostgres.builder()
                    .start();
            applyDatabaseProperties();
            ensureDatabaseExists();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to start embedded PostgreSQL", e);
        }
    }

    public static synchronized void stop() {
        if (embeddedPostgres == null) {
            return;
        }

        try {
            embeddedPostgres.close();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to stop embedded PostgreSQL", e);
        } finally {
            embeddedPostgres = null;
            clearDatabaseProperties();
        }
    }

    private static void applyDatabaseProperties() {
        System.setProperty("bankinfo.db.host", HOST);
        System.setProperty("bankinfo.db.port", String.valueOf(embeddedPostgres.getPort()));
        System.setProperty("bankinfo.db.name", DB_NAME);
        System.setProperty("bankinfo.db.user", DB_USER);
        System.setProperty("bankinfo.db.password", DB_PASSWORD);
    }

    private static void clearDatabaseProperties() {
        System.clearProperty("bankinfo.db.host");
        System.clearProperty("bankinfo.db.port");
        System.clearProperty("bankinfo.db.name");
        System.clearProperty("bankinfo.db.user");
        System.clearProperty("bankinfo.db.password");
    }

    private static void ensureDatabaseExists() {
        try (Connection connection = embeddedPostgres.getPostgresDatabase().getConnection();
             Statement statement = connection.createStatement()) {
            try (ResultSet rs = statement.executeQuery("select 1 from pg_database where datname = '" + DB_NAME + "'")) {
                if (!rs.next()) {
                    statement.execute("create database " + DB_NAME);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to ensure test database existence", e);
        }
    }
}
