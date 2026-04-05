package bankinfo.model;

import org.testng.annotations.Test;

import java.time.OffsetDateTime;

import static org.testng.Assert.*;

public class ContactPersonTest {

    @Test
    public void gettersSettersAndToString_shouldWork() {
        ContactPerson contactPerson = new ContactPerson(
                "Ivan Petrov",
                "+7-999-111-11-11",
                "ivan@example.com",
                "Manager"
        );

        Client client = new Client(
                ClientType.ORG,
                "Test Client",
                OffsetDateTime.parse("2026-03-01T10:00:00+03:00")
        );

        contactPerson.setClient(client);
        contactPerson.setFullName("Petr Sidorov");
        contactPerson.setPhone("+7-999-222-22-22");
        contactPerson.setEmail("petr@example.com");
        contactPerson.setPosition("Director");

        assertSame(contactPerson.getClient(), client);
        assertEquals(contactPerson.getFullName(), "Petr Sidorov");
        assertEquals(contactPerson.getPhone(), "+7-999-222-22-22");
        assertEquals(contactPerson.getEmail(), "petr@example.com");
        assertEquals(contactPerson.getPosition(), "Director");

        String text = contactPerson.toString();
        assertNotNull(text);
        assertTrue(text.contains("ContactPerson{"));
        assertTrue(text.contains("fullName='Petr Sidorov'"));
        assertTrue(text.contains("phone='+7-999-222-22-22'"));
        assertTrue(text.contains("email='petr@example.com'"));
        assertTrue(text.contains("position='Director'"));
    }
}