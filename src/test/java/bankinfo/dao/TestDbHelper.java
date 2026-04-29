package bankinfo.dao;

import bankinfo.test.EmbeddedPostgresHolder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.nio.charset.StandardCharsets;

public final class TestDbHelper {

    private TestDbHelper() {
    }

    public static void recreateAndFillDatabase() {
        EmbeddedPostgresHolder.start();
        runSql("db/create.sql");
        runSql("db/clean.sql");
        runSql("db/init.sql");
    }

    private static void runSql(String resourcePath) {
        try {
            InputStream inputStream = TestDbHelper.class.getClassLoader().getResourceAsStream(resourcePath);
            if (inputStream == null) {
                throw new IllegalStateException("SQL resource not found: " + resourcePath);
            }

            String sql;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                sql = reader.lines().reduce("", (a, b) -> a + b + "\n");
            }

            String dbHost = System.getProperty("bankinfo.db.host", "127.0.0.1");
            String dbPort = System.getProperty("bankinfo.db.port", "5432");
            String dbName = System.getProperty("bankinfo.db.name", "bankinfo");
            String dbUser = System.getProperty("bankinfo.db.user", "postgres");
            String dbPassword = System.getProperty("bankinfo.db.password", "postgres");
            String jdbcUrl = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName;

            try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
                 Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute SQL resource: " + resourcePath, e);
        }
    }
}
