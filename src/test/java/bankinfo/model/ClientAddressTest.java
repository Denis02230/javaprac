package bankinfo.model;

import org.testng.annotations.Test;

import java.time.OffsetDateTime;

import static org.testng.Assert.*;

public class ClientAddressTest {

    @Test
    public void gettersSettersAndToString_shouldWork() {
        ClientAddress address = new ClientAddress(
                "Moscow, Old st, 1",
                AddressType.LEGAL
        );

        Client client = new Client(
                ClientType.ORG,
                "Test Client",
                OffsetDateTime.parse("2026-03-01T10:00:00+03:00")
        );

        address.setClient(client);
        address.setAddress("Saint Petersburg, New st, 2");
        address.setAddressType(AddressType.POSTAL);

        assertNull(address.getId());
        assertSame(address.getClient(), client);
        assertEquals(address.getAddress(), "Saint Petersburg, New st, 2");
        assertEquals(address.getAddressType(), AddressType.POSTAL);

        String text = address.toString();
        assertNotNull(text);
        assertTrue(text.contains("ClientAddress{"));
        assertTrue(text.contains("id=null"));
        assertTrue(text.contains("address='Saint Petersburg, New st, 2'"));
        assertTrue(text.contains("addressType=POSTAL"));
    }
}