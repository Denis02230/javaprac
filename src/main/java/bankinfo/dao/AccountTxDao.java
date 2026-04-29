package bankinfo.dao;

import bankinfo.model.AccountTx;
import bankinfo.model.TxType;
import bankinfo.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class AccountTxDao {

    public List<AccountTx> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select t from AccountTx t order by t.id",
                    AccountTx.class
            ).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load all account transactions", e);
        }
    }

    public List<AccountTx> findAllDetailed() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select t from AccountTx t " +
                    "join fetch t.account a " +
                    "join fetch a.client " +
                    "join fetch a.branch " +
                    "join fetch a.accountType " +
                    "order by t.txTime, t.id",
                    AccountTx.class
            ).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load all detailed account transactions", e);
        }
    }

    public Optional<AccountTx> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(AccountTx.class, id));
        } catch (Exception e) {
            throw new DaoException("Failed to load transaction by id=" + id, e);
        }
    }

    public Optional<AccountTx> findByIdDetailed(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<AccountTx> result = session.createQuery(
                    "select t from AccountTx t " +
                    "join fetch t.account a " +
                    "join fetch a.client " +
                    "join fetch a.branch " +
                    "join fetch a.accountType " +
                    "where t.id = :id",
                    AccountTx.class
            ).setParameter("id", id).list();

            return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        } catch (Exception e) {
            throw new DaoException("Failed to load detailed transaction by id=" + id, e);
        }
    }

    public List<AccountTx> findByAccountId(Long accountId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select t from AccountTx t " +
                    "join fetch t.account a " +
                    "where a.id = :accountId " +
                    "order by t.txTime, t.id",
                    AccountTx.class
            ).setParameter("accountId", accountId).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load transactions by accountId=" + accountId, e);
        }
    }

    public List<AccountTx> findByPeriodDetailed(OffsetDateTime fromInclusive, OffsetDateTime toInclusive) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select t from AccountTx t " +
                    "join fetch t.account a " +
                    "join fetch a.client " +
                    "join fetch a.branch " +
                    "join fetch a.accountType " +
                    "where t.txTime >= :fromTime and t.txTime <= :toTime " +
                    "order by t.txTime, t.id",
                    AccountTx.class
            )
            .setParameter("fromTime", fromInclusive)
            .setParameter("toTime", toInclusive)
            .list();
        } catch (Exception e) {
            throw new DaoException("Failed to load detailed transactions by period", e);
        }
    }

    public List<AccountTx> findByType(TxType txType) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select t from AccountTx t " +
                    "where t.txType = :txType " +
                    "order by t.id",
                    AccountTx.class
            ).setParameter("txType", txType).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load transactions by type=" + txType, e);
        }
    }

    public List<AccountTx> findByAccountIdAndType(Long accountId, TxType txType) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select t from AccountTx t " +
                    "join fetch t.account a " +
                    "where a.id = :accountId and t.txType = :txType " +
                    "order by t.txTime, t.id",
                    AccountTx.class
            )
            .setParameter("accountId", accountId)
            .setParameter("txType", txType)
            .list();
        } catch (Exception e) {
            throw new DaoException("Failed to load transactions by accountId and type", e);
        }
    }

    public List<AccountTx> findByPeriod(OffsetDateTime fromInclusive, OffsetDateTime toInclusive) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select t from AccountTx t " +
                    "where t.txTime >= :fromTime and t.txTime <= :toTime " +
                    "order by t.txTime, t.id",
                    AccountTx.class
            )
            .setParameter("fromTime", fromInclusive)
            .setParameter("toTime", toInclusive)
            .list();
        } catch (Exception e) {
            throw new DaoException("Failed to load transactions by period", e);
        }
    }

    public List<AccountTx> findWithAmountGreaterThan(BigDecimal amount) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select t from AccountTx t " +
                    "where t.amount > :amount " +
                    "order by t.id",
                    AccountTx.class
            ).setParameter("amount", amount).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load transactions by amount threshold", e);
        }
    }

    public AccountTx save(AccountTx txObject) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.saveOrUpdate(txObject);
            tx.commit();
            return txObject;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new DaoException("Failed to save transaction", e);
        }
    }
}
