package bankinfo.util;

import bankinfo.model.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {

    private static SessionFactory sessionFactory;

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration()
                    .configure()
                    .addAnnotatedClass(Branch.class)
                    .addAnnotatedClass(Client.class)
                    .addAnnotatedClass(ClientAddress.class)
                    .addAnnotatedClass(AccountType.class)
                    .addAnnotatedClass(Account.class)
                    .addAnnotatedClass(AccountTx.class)
                    .addAnnotatedClass(ClientPhone.class)
                    .addAnnotatedClass(ClientEmail.class)
                    .addAnnotatedClass(ContactPerson.class);

            String dbHost = System.getProperty("bankinfo.db.host");
            String dbPort = System.getProperty("bankinfo.db.port");
            String dbName = System.getProperty("bankinfo.db.name");
            String dbUser = System.getProperty("bankinfo.db.user");
            String dbPassword = System.getProperty("bankinfo.db.password");

            if (dbHost != null || dbPort != null || dbName != null) {
                String host = dbHost == null ? "127.0.0.1" : dbHost;
                String port = dbPort == null ? "5432" : dbPort;
                String name = dbName == null ? "bankinfo" : dbName;
                configuration.setProperty("hibernate.connection.url", "jdbc:postgresql://" + host + ":" + port + "/" + name);
            }

            if (dbUser != null) {
                configuration.setProperty("hibernate.connection.username", dbUser);
            }

            if (dbPassword != null) {
                configuration.setProperty("hibernate.connection.password", dbPassword);
            }

            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to build SessionFactory");
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            sessionFactory = buildSessionFactory();
        }
        return sessionFactory;
    }

    public static synchronized void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
