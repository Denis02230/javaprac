package bankinfo.model;

import org.testng.annotations.Test;

import java.time.OffsetDateTime;

import static org.testng.Assert.*;

public class ClientPhoneTest {

    @Test
    public void gettersSettersAndToString_shouldWork() {
        ClientPhone phone = new ClientPhone(
                "+7-999-111-11-11",
                PhoneType.MOBILE
        );

        Client client = new Client(
                ClientType.ORG,
                "Test Client",
                OffsetDateTime.parse("2026-03-01T10:00:00+03:00")
        );

        phone.setClient(client);
        phone.setPhone("+7-999-222-22-22");
        phone.setPhoneType(PhoneType.WORK);

        assertNull(phone.getId());
        assertSame(phone.getClient(), client);
        assertEquals(phone.getPhone(), "+7-999-222-22-22");
        assertEquals(phone.getPhoneType(), PhoneType.WORK);

        String text = phone.toString();
        assertNotNull(text);
        assertTrue(text.contains("ClientPhone{"));
        assertTrue(text.contains("id=null"));
        assertTrue(text.contains("phone='+7-999-222-22-22'"));
        assertTrue(text.contains("phoneType=WORK"));
    }
}