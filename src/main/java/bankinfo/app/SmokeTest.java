package bankinfo.app;

import bankinfo.model.Account;
import bankinfo.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class SmokeTest {

    public static void main(String[] args) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();

            List<Account> accounts = session.createQuery(
                    "select a from Account a " +
                    "join fetch a.client " +
                    "join fetch a.branch " +
                    "join fetch a.accountType " +
                    "left join fetch a.interestTargetAccount " +
                    "order by a.id",
                    Account.class
            ).list();

            tx.commit();
            session.close();

            System.out.println("Loaded accounts: " + accounts.size());

            for (Account account : accounts) {
                System.out.println(account);
                System.out.println("  client=" + account.getClient().getDisplayName());
                System.out.println("  branch=" + account.getBranch().getName());
                System.out.println("  accountType=" + account.getAccountType().getName());

                if (account.getInterestTargetAccount() != null) {
                    System.out.println("  interestTarget=" + account.getInterestTargetAccount().getAccountNumber());
                } else {
                    System.out.println("  interestTarget=null");
                }
            }
        } finally {
            HibernateUtil.shutdown();
        }
    }
}