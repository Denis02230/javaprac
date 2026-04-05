package bankinfo.model;

import org.testng.annotations.Test;

import java.time.OffsetDateTime;

import static org.testng.Assert.*;

public class ClientTest {

    @Test
    public void addAddress_shouldAddAddressAndSetBackReference() {
        Client client = new Client(
                ClientType.ORG,
                "Test Client",
                OffsetDateTime.parse("2026-03-01T10:00:00+03:00")
        );
        ClientAddress address = new ClientAddress(
                "Moscow, Test st, 1",
                AddressType.LEGAL
        );

        client.addAddress(address);

        assertEquals(client.getAddresses().size(), 1);
        assertSame(client.getAddresses().get(0), address);
        assertSame(address.getClient(), client);
    }

    @Test
    public void removeAddress_shouldRemoveAddressAndClearBackReference() {
        Client client = new Client(
                ClientType.ORG,
                "Test Client",
                OffsetDateTime.parse("2026-03-01T10:00:00+03:00")
        );
        ClientAddress address = new ClientAddress(
                "Moscow, Test st, 1",
                AddressType.LEGAL
        );
        client.addAddress(address);

        client.removeAddress(address);

        assertTrue(client.getAddresses().isEmpty());
        assertNull(address.getClient());
    }

    @Test
    public void addPhone_shouldAddPhoneAndSetBackReference() {
        Client client = new Client(
                ClientType.PERSON,
                "Test Client",
                OffsetDateTime.parse("2026-03-01T10:00:00+03:00")
        );
        ClientPhone phone = new ClientPhone(
                "+7-999-111-11-11",
                PhoneType.MOBILE
        );

        client.addPhone(phone);

        assertEquals(client.getPhones().size(), 1);
        assertSame(client.getPhones().get(0), phone);
        assertSame(phone.getClient(), client);
    }

    @Test
    public void removePhone_shouldRemovePhoneAndClearBackReference() {
        Client client = new Client(
                ClientType.PERSON,
                "Test Client",
                OffsetDateTime.parse("2026-03-01T10:00:00+03:00")
        );
        ClientPhone phone = new ClientPhone(
                "+7-999-111-11-11",
                PhoneType.MOBILE
        );
        client.addPhone(phone);

        client.removePhone(phone);

        assertTrue(client.getPhones().isEmpty());
        assertNull(phone.getClient());
    }

    @Test
    public void addEmail_shouldAddEmailAndSetBackReference() {
        Client client = new Client(
                ClientType.PERSON,
                "Test Client",
                OffsetDateTime.parse("2026-03-01T10:00:00+03:00")
        );
        ClientEmail email = new ClientEmail(
                "test@example.com",
                EmailType.MAIN
        );

        client.addEmail(email);

        assertEquals(client.getEmails().size(), 1);
        assertSame(client.getEmails().get(0), email);
        assertSame(email.getClient(), client);
    }

    @Test
    public void removeEmail_shouldRemoveEmailAndClearBackReference() {
        Client client = new Client(
                ClientType.PERSON,
                "Test Client",
                OffsetDateTime.parse("2026-03-01T10:00:00+03:00")
        );
        ClientEmail email = new ClientEmail(
                "test@example.com",
                EmailType.MAIN
        );
        client.addEmail(email);

        client.removeEmail(email);

        assertTrue(client.getEmails().isEmpty());
        assertNull(email.getClient());
    }

    @Test
    public void addContactPerson_shouldAddContactAndSetBackReference() {
        Client client = new Client(
                ClientType.ORG,
                "Test Client",
                OffsetDateTime.parse("2026-03-01T10:00:00+03:00")
        );
        ContactPerson contactPerson = new ContactPerson(
                "Test Manager",
                "+7-999-222-22-22",
                "manager@example.com",
                "Manager"
        );

        client.addContactPerson(contactPerson);

        assertEquals(client.getContactPersons().size(), 1);
        assertSame(client.getContactPersons().get(0), contactPerson);
        assertSame(contactPerson.getClient(), client);
    }

    @Test
    public void removeContactPerson_shouldRemoveContactAndClearBackReference() {
        Client client = new Client(
                ClientType.ORG,
                "Test Client",
                OffsetDateTime.parse("2026-03-01T10:00:00+03:00")
        );
        ContactPerson contactPerson = new ContactPerson(
                "Test Manager",
                "+7-999-222-22-22",
                "manager@example.com",
                "Manager"
        );
        client.addContactPerson(contactPerson);

        client.removeContactPerson(contactPerson);

        assertTrue(client.getContactPersons().isEmpty());
        assertNull(contactPerson.getClient());
    }

    @Test
    public void gettersSettersAndToString_shouldWork() {
    OffsetDateTime createdAt = OffsetDateTime.parse("2026-03-01T10:00:00+03:00");

    Client client = new Client(
            ClientType.PERSON,
            "Initial Name",
            createdAt
    );

    client.setClientType(ClientType.ORG);
    client.setDisplayName("Updated Name");

    OffsetDateTime updatedCreatedAt = OffsetDateTime.parse("2026-03-02T11:30:00+03:00");
    client.setCreatedAt(updatedCreatedAt);

    assertEquals(client.getClientType(), ClientType.ORG);
    assertEquals(client.getDisplayName(), "Updated Name");
    assertEquals(client.getCreatedAt(), updatedCreatedAt);

    assertNotNull(client.getAccounts());
    assertTrue(client.getAccounts().isEmpty());

    String text = client.toString();
    assertNotNull(text);
    assertTrue(text.contains("Client{"));
    assertTrue(text.contains("clientType=ORG"));
    assertTrue(text.contains("displayName='Updated Name'"));
    assertTrue(text.contains("createdAt=2026-03-02T11:30+03:00"));
    }
}