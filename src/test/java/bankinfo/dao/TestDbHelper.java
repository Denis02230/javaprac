package bankinfo.dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class TestDbHelper {

    private static final String DB_HOST = "127.0.0.1";
    private static final String DB_PORT = "5432";
    private static final String DB_NAME = "bankinfo";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "postgres";

    private TestDbHelper() {
    }

    public static void recreateAndFillDatabase() {
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

            ProcessBuilder pb = new ProcessBuilder(
                    "psql",
                    "-v", "ON_ERROR_STOP=1",
                    "-h", DB_HOST,
                    "-p", DB_PORT,
                    "-U", DB_USER,
                    "-d", DB_NAME
            );

            pb.environment().put("PGPASSWORD", DB_PASSWORD);

            Process process = pb.start();
            process.getOutputStream().write(sql.getBytes(StandardCharsets.UTF_8));
            process.getOutputStream().close();

            String stderr = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            String stdout = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IllegalStateException(
                        "psql failed for " + resourcePath + "\nSTDOUT:\n" + stdout + "\nSTDERR:\n" + stderr
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute SQL resource: " + resourcePath, e);
        }
    }
}