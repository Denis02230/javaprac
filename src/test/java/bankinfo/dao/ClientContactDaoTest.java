package bankinfo.dao;

import bankinfo.model.Client;
import bankinfo.model.ClientEmail;
import bankinfo.model.ClientPhone;
import bankinfo.model.ContactPerson;
import bankinfo.model.EmailType;
import bankinfo.model.PhoneType;
import bankinfo.util.HibernateUtil;
import org.hibernate.Session;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class ClientContactDaoTest {

    private final ClientContactDao clientContactDao = new ClientContactDao();

    @BeforeMethod
    public void resetDatabase() {
        TestDbHelper.recreateAndFillDatabase();
    }

    @Test
    public void findPhonesByClientId_shouldReturnClientPhones() {
        List<ClientPhone> phones = clientContactDao.findPhonesByClientId(4L);

        assertEquals(phones.size(), 1);
        assertEquals(phones.get(0).getPhone(), "+7-916-510-44-00");
        assertEquals(phones.get(0).getPhoneType(), PhoneType.WORK);
        assertEquals(phones.get(0).getClient().getId(), Long.valueOf(4L));
    }

    @Test
    public void findPhonesByClientId_shouldReturnEmpty_whenNoMatches() {
        List<ClientPhone> phones = clientContactDao.findPhonesByClientId(999L);

        assertNotNull(phones);
        assertTrue(phones.isEmpty());
    }

    @Test
    public void findPhonesByType_shouldReturnOnlyMatchingType() {
        List<ClientPhone> phones = clientContactDao.findPhonesByType(PhoneType.OTHER);

        assertEquals(phones.size(), 2);
        assertTrue(phones.stream().allMatch(p -> p.getPhoneType() == PhoneType.OTHER));
        assertEquals(phones.get(0).getId(), Long.valueOf(5L));
        assertEquals(phones.get(1).getId(), Long.valueOf(6L));
    }

    @Test
    public void findEmailsByClientId_shouldReturnClientEmails() {
        List<ClientEmail> emails = clientContactDao.findEmailsByClientId(4L);

        assertEquals(emails.size(), 2);
        assertEquals(emails.get(0).getEmail(), "a.murmov@example.com");
        assertEquals(emails.get(0).getEmailType(), EmailType.MAIN);
        assertEquals(emails.get(1).getEmail(), "billing@alpha.example.com");
        assertEquals(emails.get(1).getEmailType(), EmailType.BILLING);
        assertTrue(emails.stream().allMatch(e -> e.getClient().getId().equals(4L)));
    }

    @Test
    public void findEmailsByClientId_shouldReturnEmpty_whenNoMatches() {
        List<ClientEmail> emails = clientContactDao.findEmailsByClientId(999L);

        assertNotNull(emails);
        assertTrue(emails.isEmpty());
    }

    @Test
    public void findEmailsByType_shouldReturnOnlyMatchingType() {
        List<ClientEmail> emails = clientContactDao.findEmailsByType(EmailType.BILLING);

        assertEquals(emails.size(), 2);
        assertTrue(emails.stream().allMatch(e -> e.getEmailType() == EmailType.BILLING));
        assertEquals(emails.get(0).getId(), Long.valueOf(7L));
        assertEquals(emails.get(1).getId(), Long.valueOf(8L));
    }

    @Test
    public void findContactPersonsByClientId_shouldReturnClientContacts() {
        List<ContactPerson> contacts = clientContactDao.findContactPersonsByClientId(4L);

        assertEquals(contacts.size(), 2);
        assertEquals(contacts.get(0).getFullName(), "Arman Hakobyan");
        assertEquals(contacts.get(1).getFullName(), "Mariam Grigoryan");
        assertTrue(contacts.stream().allMatch(cp -> cp.getClient().getId().equals(4L)));
    }

    @Test
    public void findContactPersonsByClientId_shouldReturnEmpty_whenNoMatches() {
        List<ContactPerson> contacts = clientContactDao.findContactPersonsByClientId(1L);

        assertNotNull(contacts);
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void findContactPersonsWithEmail_shouldReturnOnlyRowsWithEmail() {
        List<ContactPerson> contacts = clientContactDao.findContactPersonsWithEmail();

        assertEquals(contacts.size(), 3);
        assertTrue(contacts.stream().allMatch(cp -> cp.getEmail() != null));
        assertEquals(contacts.get(0).getFullName(), "Arman Hakobyan");
        assertEquals(contacts.get(1).getFullName(), "Mariam Grigoryan");
        assertEquals(contacts.get(2).getFullName(), "Lilit Hovsepyan");
    }

    @Test
    public void savePhone_shouldInsertNewPhone() {
        ClientPhone phone = new ClientPhone("+7-999-777-77-77", PhoneType.MOBILE);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Client client = session.get(Client.class, 1L);
            phone.setClient(client);
        }

        ClientPhone saved = clientContactDao.savePhone(phone);

        assertNotNull(saved.getId());

        List<ClientPhone> phones = clientContactDao.findPhonesByClientId(1L);
        assertEquals(phones.size(), 2);
        assertEquals(phones.get(1).getPhone(), "+7-999-777-77-77");
        assertEquals(phones.get(1).getPhoneType(), PhoneType.MOBILE);
    }

    @Test
    public void saveEmail_shouldInsertNewEmail() {
        ClientEmail email = new ClientEmail("new.extra@example.com", EmailType.OTHER);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Client client = session.get(Client.class, 1L);
            email.setClient(client);
        }

        ClientEmail saved = clientContactDao.saveEmail(email);

        assertNotNull(saved.getId());

        List<ClientEmail> emails = clientContactDao.findEmailsByClientId(1L);
        assertEquals(emails.size(), 2);
        assertEquals(emails.get(1).getEmail(), "new.extra@example.com");
        assertEquals(emails.get(1).getEmailType(), EmailType.OTHER);
    }

    @Test
    public void saveContactPerson_shouldInsertNewContactPerson() {
        ContactPerson contactPerson = new ContactPerson(
                "Test Contact",
                "+7-999-333-22-11",
                "test.contact@example.com",
                "Assistant"
        );

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Client client = session.get(Client.class, 6L);
            contactPerson.setClient(client);
        }

        ContactPerson saved = clientContactDao.saveContactPerson(contactPerson);

        assertNotNull(saved.getId());

        List<ContactPerson> contacts = clientContactDao.findContactPersonsByClientId(6L);
        assertEquals(contacts.size(), 2);
        assertEquals(contacts.get(1).getFullName(), "Test Contact");
        assertEquals(contacts.get(1).getEmail(), "test.contact@example.com");
        assertEquals(contacts.get(1).getPosition(), "Assistant");
    }
}