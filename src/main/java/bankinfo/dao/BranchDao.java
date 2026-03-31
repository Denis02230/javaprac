package bankinfo.dao;

import bankinfo.model.Branch;
import bankinfo.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class BranchDao {

    public List<Branch> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select b from Branch b order by b.id",
                    Branch.class
            ).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load all branches", e);
        }
    }

    public Optional<Branch> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Branch branch = session.get(Branch.class, id);
            return Optional.ofNullable(branch);
        } catch (Exception e) {
            throw new DaoException("Failed to load branch by id=" + id, e);
        }
    }

    public Optional<Branch> findByIdWithAccounts(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Branch> result = session.createQuery(
                    "select distinct b from Branch b " +
                    "left join fetch b.accounts a " +
                    "where b.id = :id " +
                    "order by a.id",
                    Branch.class
            ).setParameter("id", id).list();

            if (result.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(result.get(0));
        } catch (Exception e) {
            throw new DaoException("Failed to load branch with accounts by id=" + id, e);
        }
    }

    public List<Branch> findByNameContains(String text) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select b from Branch b " +
                    "where lower(b.name) like :pattern " +
                    "order by b.id",
                    Branch.class
            ).setParameter("pattern", "%" + text.toLowerCase() + "%")
             .list();
        } catch (Exception e) {
            throw new DaoException("Failed to search branches by name", e);
        }
    }

    public List<Branch> findBranchesWithOpenAccounts() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select distinct b from Branch b " +
                    "join b.accounts a " +
                    "where a.closedAt is null " +
                    "order by b.id",
                    Branch.class
            ).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load branches with open accounts", e);
        }
    }

    public Branch save(Branch branch) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.saveOrUpdate(branch);
            tx.commit();
            return branch;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new DaoException("Failed to save branch", e);
        }
    }
}