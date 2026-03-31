package bankinfo.dao;

import bankinfo.model.AccountType;
import bankinfo.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class AccountTypeDao {

    public List<AccountType> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select at from AccountType at order by at.id",
                    AccountType.class
            ).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load all account types", e);
        }
    }

    public Optional<AccountType> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            AccountType accountType = session.get(AccountType.class, id);
            return Optional.ofNullable(accountType);
        } catch (Exception e) {
            throw new DaoException("Failed to load account type by id=" + id, e);
        }
    }

    public List<AccountType> findByNameContains(String text) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select at from AccountType at " +
                    "where lower(at.name) like :pattern " +
                    "order by at.id",
                    AccountType.class
            ).setParameter("pattern", "%" + text.toLowerCase() + "%")
             .list();
        } catch (Exception e) {
            throw new DaoException("Failed to search account types by name", e);
        }
    }

    public List<AccountType> findDebitAllowed() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select at from AccountType at " +
                    "where at.allowDebit = true " +
                    "order by at.id",
                    AccountType.class
            ).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load debit-allowed account types", e);
        }
    }

    public List<AccountType> findCreditAllowed() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select at from AccountType at " +
                    "where at.allowCredit = true " +
                    "order by at.id",
                    AccountType.class
            ).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load credit-allowed account types", e);
        }
    }

    public List<AccountType> findWithInterestRateGreaterThan(BigDecimal value) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select at from AccountType at " +
                    "where at.interestRate > :value " +
                    "order by at.id",
                    AccountType.class
            ).setParameter("value", value)
             .list();
        } catch (Exception e) {
            throw new DaoException("Failed to load account types by interest rate", e);
        }
    }

    public AccountType save(AccountType accountType) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.saveOrUpdate(accountType);
            tx.commit();
            return accountType;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new DaoException("Failed to save account type", e);
        }
    }
}