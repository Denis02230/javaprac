package bankinfo.dao;

import bankinfo.model.Client;
import bankinfo.model.ClientType;
import bankinfo.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ClientDao {

    public List<Client> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select c from Client c order by c.id",
                    Client.class
            ).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load all clients", e);
        }
    }

    public Optional<Client> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Client client = session.get(Client.class, id);
            return Optional.ofNullable(client);
        } catch (Exception e) {
            throw new DaoException("Failed to load client by id=" + id, e);
        }
    }

    public Optional<Client> findByIdWithAddresses(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Client> result = session.createQuery(
                    "select distinct c from Client c " +
                    "left join fetch c.addresses " +
                    "where c.id = :id",
                    Client.class
            ).setParameter("id", id).list();

            if (result.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(result.get(0));
        } catch (Exception e) {
            throw new DaoException("Failed to load client with addresses by id=" + id, e);
        }
    }

    public Optional<Client> findByIdWithContacts(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Client client = session.get(Client.class, id);

            if (client == null) {
                return Optional.empty();
            }

            client.getPhones().size();
            client.getEmails().size();
            client.getContactPersons().size();

            return Optional.of(client);
        } catch (Exception e) {
            throw new DaoException("Failed to load client with contacts by id=" + id, e);
        }
    }

    public Optional<Client> findByIdDetailed(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Client client = session.get(Client.class, id);
            if (client == null) {
                return Optional.empty();
            }

            client.getAddresses().size();
            client.getPhones().size();
            client.getEmails().size();
            client.getContactPersons().size();

            return Optional.of(client);
        } catch (Exception e) {
            throw new DaoException("Failed to load detailed client by id=" + id, e);
        }
    }

    public List<Client> findByType(ClientType clientType) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select c from Client c " +
                    "where c.clientType = :clientType " +
                    "order by c.id",
                    Client.class
            ).setParameter("clientType", clientType).list();
        } catch (Exception e) {
            throw new DaoException("Failed to load clients by type=" + clientType, e);
        }
    }

    public List<Client> findByDisplayNameContains(String text) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select c from Client c " +
                    "where lower(c.displayName) like :pattern " +
                    "order by c.id",
                    Client.class
            ).setParameter("pattern", "%" + text.toLowerCase() + "%")
             .list();
        } catch (Exception e) {
            throw new DaoException("Failed to search clients by display name", e);
        }
    }

    public List<Client> findOrganizationsWithContactPersons() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select distinct c from Client c " +
                    "join fetch c.contactPersons cp " +
                    "where c.clientType = :clientType " +
                    "order by c.id",
                    Client.class
            ).setParameter("clientType", ClientType.ORG)
             .list();
        } catch (Exception e) {
            throw new DaoException("Failed to load organizations with contact persons", e);
        }
    }

    public Client save(Client client) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.saveOrUpdate(client);
            tx.commit();
            return client;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new DaoException("Failed to save client", e);
        }
    }

    public void deleteById(Long id) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Client client = session.get(Client.class, id);
            if (client != null) {
                session.delete(client);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new DaoException("Failed to delete client by id=" + id, e);
        }
    }
}
