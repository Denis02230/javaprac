package bankinfo.dao;

import bankinfo.model.AddressType;
import bankinfo.model.Client;
import bankinfo.model.ClientAddress;
import bankinfo.model.ClientEmail;
import bankinfo.model.ClientPhone;
import bankinfo.model.ClientType;
import bankinfo.model.ContactPerson;
import bankinfo.model.EmailType;
import bankinfo.model.PhoneType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

public class ClientDaoTest {

    private final ClientDao clientDao = new ClientDao();

    @BeforeMethod
    public void resetDatabase() {
        TestDbHelper.recreateAndFillDatabase();
    }

    @Test
    public void findAll_shouldReturnAllClients() {
        List<Client> clients = clientDao.findAll();

        assertNotNull(clients);
        assertEquals(clients.size(), 6);
        assertEquals(clients.get(0).getDisplayName(), "Ivan Petrov");
        assertEquals(clients.get(5).getDisplayName(), "Gamma NGO");
    }

    @Test
    public void findById_shouldReturnClient_whenClientExists() {
        Optional<Client> result = clientDao.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(result.get().getDisplayName(), "Ivan Petrov");
        assertEquals(result.get().getClientType(), ClientType.PERSON);
    }

    @Test
    public void findById_shouldReturnEmpty_whenClientDoesNotExist() {
        Optional<Client> result = clientDao.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findByIdWithAddresses_shouldLoadAddresses() {
        Optional<Client> result = clientDao.findByIdWithAddresses(4L);

        assertTrue(result.isPresent());

        Client client = result.get();
        assertEquals(client.getDisplayName(), "Alpha LLC");
        assertEquals(client.getAddresses().size(), 2);

        ClientAddress first = client.getAddresses().get(0);
        assertNotNull(first.getAddress());
        assertNotNull(first.getAddressType());
    }

    @Test
    public void findByIdWithAddresses_shouldReturnEmpty_whenClientDoesNotExist() {
        Optional<Client> result = clientDao.findByIdWithAddresses(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findByIdWithContacts_shouldLoadPhonesEmailsAndContactPersons() {
        Optional<Client> result = clientDao.findByIdWithContacts(4L);

        assertTrue(result.isPresent());

        Client client = result.get();
        assertEquals(client.getDisplayName(), "Alpha LLC");

        assertEquals(client.getPhones().size(), 1);
        ClientPhone phone = client.getPhones().get(0);
        assertEquals(phone.getPhone(), "+7-916-510-44-00");
        assertEquals(phone.getPhoneType(), PhoneType.WORK);

        assertEquals(client.getEmails().size(), 2);
        assertTrue(client.getEmails().stream().map(ClientEmail::getEmail).anyMatch(e -> e.equals("a.murmov@example.com")));
        assertTrue(client.getEmails().stream().map(ClientEmail::getEmailType).anyMatch(t -> t == EmailType.MAIN));
        assertTrue(client.getEmails().stream().map(ClientEmail::getEmailType).anyMatch(t -> t == EmailType.BILLING));

        assertEquals(client.getContactPersons().size(), 2);
        assertTrue(client.getContactPersons().stream().map(ContactPerson::getFullName).anyMatch(n -> n.equals("Arman Hakobyan")));
        assertTrue(client.getContactPersons().stream().map(ContactPerson::getFullName).anyMatch(n -> n.equals("Mariam Grigoryan")));
    }

    @Test
    public void findByIdWithContacts_shouldReturnEmpty_whenClientDoesNotExist() {
        Optional<Client> result = clientDao.findByIdWithContacts(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findByType_shouldReturnOnlyPersons() {
        List<Client> clients = clientDao.findByType(ClientType.PERSON);

        assertEquals(clients.size(), 3);
        assertTrue(clients.stream().allMatch(c -> c.getClientType() == ClientType.PERSON));
    }

    @Test
    public void findByType_shouldReturnOnlyOrganizations() {
        List<Client> clients = clientDao.findByType(ClientType.ORG);

        assertEquals(clients.size(), 3);
        assertTrue(clients.stream().allMatch(c -> c.getClientType() == ClientType.ORG));
    }

    @Test
    public void findByDisplayNameContains_shouldReturnMatches_caseInsensitive() {
        List<Client> clients = clientDao.findByDisplayNameContains("alpha");

        assertEquals(clients.size(), 1);
        assertEquals(clients.get(0).getDisplayName(), "Alpha LLC");
    }

    @Test
    public void findByDisplayNameContains_shouldReturnEmpty_whenNoMatches() {
        List<Client> clients = clientDao.findByDisplayNameContains("zzz");

        assertNotNull(clients);
        assertTrue(clients.isEmpty());
    }

    @Test
    public void findOrganizationsWithContactPersons_shouldReturnOnlyOrganizationsThatHaveContacts() {
        List<Client> clients = clientDao.findOrganizationsWithContactPersons();

        assertEquals(clients.size(), 3);
        assertTrue(clients.stream().allMatch(c -> c.getClientType() == ClientType.ORG));
        assertTrue(clients.stream().allMatch(c -> !c.getContactPersons().isEmpty()));
        assertEquals(clients.get(0).getDisplayName(), "Alpha LLC");
        assertEquals(clients.get(1).getDisplayName(), "Bella Trade JSC");
        assertEquals(clients.get(2).getDisplayName(), "Gamma NGO");
    }

    @Test
    public void save_shouldInsertNewClient() {
        Client client = new Client(
                ClientType.PERSON,
                "Test Person",
                OffsetDateTime.parse("2026-03-01T10:00:00+03:00")
        );

        Client saved = clientDao.save(client);

        assertNotNull(saved.getId());

        Optional<Client> loaded = clientDao.findById(saved.getId());
        assertTrue(loaded.isPresent());
        assertEquals(loaded.get().getDisplayName(), "Test Person");
        assertEquals(loaded.get().getClientType(), ClientType.PERSON);
    }

    @Test
    public void save_shouldInsertClientWithContacts() {
        Client client = new Client(
                ClientType.ORG,
                "Test Org",
                OffsetDateTime.parse("2026-03-02T10:00:00+03:00")
        );

        client.addPhone(new ClientPhone("+7-999-100-00-00", PhoneType.WORK));
        client.addEmail(new ClientEmail("info@test-org.example.com", EmailType.MAIN));
        client.addContactPerson(new ContactPerson(
                "Test Manager",
                "+7-999-200-00-00",
                "manager@test-org.example.com",
                "Manager"
        ));

        Client saved = clientDao.save(client);

        assertNotNull(saved.getId());

        Optional<Client> loaded = clientDao.findByIdWithContacts(saved.getId());
        assertTrue(loaded.isPresent());
        assertEquals(loaded.get().getDisplayName(), "Test Org");
        assertEquals(loaded.get().getPhones().size(), 1);
        assertEquals(loaded.get().getEmails().size(), 1);
        assertEquals(loaded.get().getContactPersons().size(), 1);
        assertEquals(loaded.get().getContactPersons().get(0).getFullName(), "Test Manager");
    }

    @Test
    public void save_shouldInsertClientWithAddress() {
        Client client = new Client(
                ClientType.ORG,
                "Test Org With Address",
                OffsetDateTime.parse("2026-03-03T10:00:00+03:00")
        );

        client.addAddress(new ClientAddress(
                "Moscow, Test st, 10",
                AddressType.LEGAL
        ));

        Client saved = clientDao.save(client);

        assertNotNull(saved.getId());

        Optional<Client> loaded = clientDao.findByIdWithAddresses(saved.getId());
        assertTrue(loaded.isPresent());
        assertEquals(loaded.get().getDisplayName(), "Test Org With Address");
        assertEquals(loaded.get().getAddresses().size(), 1);
        assertEquals(loaded.get().getAddresses().get(0).getAddress(), "Moscow, Test st, 10");
        assertEquals(loaded.get().getAddresses().get(0).getAddressType(), AddressType.LEGAL);
    }

    @Test
    public void findOrganizationsWithContactPersons_shouldNotReturnOrganizationWithoutContacts() {
        Client client = new Client(
                ClientType.ORG,
                "Org Without Contacts",
                OffsetDateTime.parse("2026-03-04T10:00:00+03:00")
        );
        clientDao.save(client);

        List<Client> clients = clientDao.findOrganizationsWithContactPersons();

        assertEquals(clients.size(), 3);
        assertFalse(clients.stream().anyMatch(c -> c.getDisplayName().equals("Org Without Contacts")));
    }
}