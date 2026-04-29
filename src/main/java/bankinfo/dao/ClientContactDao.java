package bankinfo.dao;

import bankinfo.model.ClientEmail;
import bankinfo.model.ClientPhone;
import bankinfo.model.ContactPerson;
import bankinfo.model.EmailType;
import bankinfo.model.PhoneType;
import bankinfo.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClientContactDao {

    public List<ClientPhone> findPhonesByClientId(Long clientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select p from ClientPhone p " +
                    "join fetch p.client c " +
                    "where c.id = :clientId " +
                    "order by p.id",
                    ClientPhone.class
            ).setParameter("clientId", clientId).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load phones by clientId=" + clientId, e);
        }
    }

    public List<ClientPhone> findPhonesByType(PhoneType phoneType) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select p from ClientPhone p " +
                    "where p.phoneType = :phoneType " +
                    "order by p.id",
                    ClientPhone.class
            ).setParameter("phoneType", phoneType).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load phones by type=" + phoneType, e);
        }
    }

    public List<ClientEmail> findEmailsByClientId(Long clientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select e from ClientEmail e " +
                    "join fetch e.client c " +
                    "where c.id = :clientId " +
                    "order by e.id",
                    ClientEmail.class
            ).setParameter("clientId", clientId).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load emails by clientId=" + clientId, e);
        }
    }

    public List<ClientEmail> findEmailsByType(EmailType emailType) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select e from ClientEmail e " +
                    "where e.emailType = :emailType " +
                    "order by e.id",
                    ClientEmail.class
            ).setParameter("emailType", emailType).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load emails by type=" + emailType, e);
        }
    }

    public List<ContactPerson> findContactPersonsByClientId(Long clientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select cp from ContactPerson cp " +
                    "join fetch cp.client c " +
                    "where c.id = :clientId " +
                    "order by cp.id",
                    ContactPerson.class
            ).setParameter("clientId", clientId).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load contact persons by clientId=" + clientId, e);
        }
    }

    public List<ContactPerson> findContactPersonsWithEmail() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select cp from ContactPerson cp " +
                    "where cp.email is not null " +
                    "order by cp.id",
                    ContactPerson.class
            ).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load contact persons with email", e);
        }
    }

    public ClientPhone savePhone(ClientPhone phone) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.saveOrUpdate(phone);
            tx.commit();
            return phone;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new DaoException("Failed to save client phone", e);
        }
    }

    public ClientEmail saveEmail(ClientEmail email) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.saveOrUpdate(email);
            tx.commit();
            return email;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new DaoException("Failed to save client email", e);
        }
    }

    public ContactPerson saveContactPerson(ContactPerson contactPerson) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.saveOrUpdate(contactPerson);
            tx.commit();
            return contactPerson;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new DaoException("Failed to save contact person", e);
        }
    }
}
