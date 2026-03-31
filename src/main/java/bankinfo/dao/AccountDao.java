package bankinfo.dao;

import bankinfo.model.Account;
import bankinfo.model.AccountStatus;
import bankinfo.model.AccountTx;
import bankinfo.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public class AccountDao {

    public List<Account> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select a from Account a order by a.id",
                    Account.class
            ).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load all accounts", e);
        }
    }

    public Optional<Account> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Account.class, id));
        } catch (Exception e) {
            throw new DaoException("Failed to load account by id=" + id, e);
        }
    }

    public Optional<Account> findByIdDetailed(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Account> result = session.createQuery(
                    "select a from Account a " +
                    "join fetch a.client " +
                    "join fetch a.branch " +
                    "join fetch a.accountType " +
                    "left join fetch a.interestTargetAccount " +
                    "where a.id = :id",
                    Account.class
            ).setParameter("id", id).list();

            return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        } catch (Exception e) {
            throw new DaoException("Failed to load detailed account by id=" + id, e);
        }
    }

    public Optional<Account> findByIdWithTransactions(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Account> result = session.createQuery(
                    "select distinct a from Account a " +
                    "left join fetch a.transactions t " +
                    "where a.id = :id " +
                    "order by t.txTime, t.id",
                    Account.class
            ).setParameter("id", id).list();

            return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        } catch (Exception e) {
            throw new DaoException("Failed to load account with transactions by id=" + id, e);
        }
    }

    public List<Account> findByClientId(Long clientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select a from Account a " +
                    "where a.client.id = :clientId " +
                    "order by a.id",
                    Account.class
            ).setParameter("clientId", clientId).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load accounts by clientId=" + clientId, e);
        }
    }

    public List<Account> findByBranchId(Long branchId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select a from Account a " +
                    "where a.branch.id = :branchId " +
                    "order by a.id",
                    Account.class
            ).setParameter("branchId", branchId).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load accounts by branchId=" + branchId, e);
        }
    }

    public List<Account> findByAccountTypeId(Long accountTypeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select a from Account a " +
                    "where a.accountType.id = :accountTypeId " +
                    "order by a.id",
                    Account.class
            ).setParameter("accountTypeId", accountTypeId).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load accounts by accountTypeId=" + accountTypeId, e);
        }
    }

    public List<Account> findByStatus(AccountStatus status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select a from Account a " +
                    "where a.status = :status " +
                    "order by a.id",
                    Account.class
            ).setParameter("status", status).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load accounts by status=" + status, e);
        }
    }

    public List<Account> findOpenAccounts() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select a from Account a " +
                    "where a.closedAt is null " +
                    "order by a.id",
                    Account.class
            ).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load open accounts", e);
        }
    }

    public List<Account> findWithBalanceGreaterThan(BigDecimal amount) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select a from Account a " +
                    "where a.balance > :amount " +
                    "order by a.id",
                    Account.class
            ).setParameter("amount", amount).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load accounts by balance threshold", e);
        }
    }

    public List<AccountTx> findTransactionsByAccountId(Long accountId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select t from AccountTx t " +
                    "join fetch t.account a " +
                    "where a.id = :accountId " +
                    "order by t.txTime, t.id",
                    AccountTx.class
            ).setParameter("accountId", accountId).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load transactions for accountId=" + accountId, e);
        }
    }

    public List<AccountTx> findTransactionsByAccountIdAndPeriod(
            Long accountId,
            OffsetDateTime fromInclusive,
            OffsetDateTime toInclusive
    ) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select t from AccountTx t " +
                    "join fetch t.account a " +
                    "where a.id = :accountId " +
                    "and t.txTime >= :fromTime " +
                    "and t.txTime <= :toTime " +
                    "order by t.txTime, t.id",
                    AccountTx.class
            )
            .setParameter("accountId", accountId)
            .setParameter("fromTime", fromInclusive)
            .setParameter("toTime", toInclusive)
            .list();
        } catch (Exception e) {
            throw new DaoException("Failed to load transactions by period for accountId=" + accountId, e);
        }
    }

    public Account save(Account account) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.saveOrUpdate(account);
            tx.commit();
            return account;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new DaoException("Failed to save account", e);
        }
    }
}