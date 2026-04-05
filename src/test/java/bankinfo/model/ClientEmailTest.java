package bankinfo.model;

import org.testng.annotations.Test;

import java.time.OffsetDateTime;

import static org.testng.Assert.*;

public class ClientEmailTest {

    @Test
    public void gettersSettersAndToString_shouldWork() {
        ClientEmail email = new ClientEmail(
                "old@example.com",
                EmailType.MAIN
        );

        Client client = new Client(
                ClientType.ORG,
                "Test Client",
                OffsetDateTime.parse("2026-03-01T10:00:00+03:00")
        );

        email.setClient(client);
        email.setEmail("new@example.com");
        email.setEmailType(EmailType.BILLING);

        assertNull(email.getId());
        assertSame(email.getClient(), client);
        assertEquals(email.getEmail(), "new@example.com");
        assertEquals(email.getEmailType(), EmailType.BILLING);

        String text = email.toString();
        assertNotNull(text);
        assertTrue(text.contains("ClientEmail{"));
        assertTrue(text.contains("id=null"));
        assertTrue(text.contains("email='new@example.com'"));
        assertTrue(text.contains("emailType=BILLING"));
    }
}