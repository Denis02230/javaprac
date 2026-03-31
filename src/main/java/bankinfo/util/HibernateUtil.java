package bankinfo.util;

import bankinfo.model.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {

    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration()
                    .configure()
                    .addAnnotatedClass(Branch.class)
                    .addAnnotatedClass(Client.class)
                    .addAnnotatedClass(ClientAddress.class)
                    .addAnnotatedClass(AccountType.class)
                    .addAnnotatedClass(Account.class)
                    .addAnnotatedClass(AccountTx.class)
                    .addAnnotatedClass(ClientPhone.class)
                    .addAnnotatedClass(ClientEmail.class)
                    .addAnnotatedClass(ContactPerson.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to build SessionFactory");
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        if (SESSION_FACTORY != null && !SESSION_FACTORY.isClosed()) {
            SESSION_FACTORY.close();
        }
    }
}