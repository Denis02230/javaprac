package bankinfo.model;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class BranchTest {

    @Test
    public void gettersSettersAndToString_shouldWork() {
        Branch branch = new Branch("Main Branch", "Old Address, 1");

        branch.setName("Updated Branch");
        branch.setAddress("New Address, 2");

        assertNull(branch.getId());
        assertEquals(branch.getName(), "Updated Branch");
        assertEquals(branch.getAddress(), "New Address, 2");

        assertNotNull(branch.getAccounts());
        assertTrue(branch.getAccounts().isEmpty());

        String text = branch.toString();
        assertNotNull(text);
        assertTrue(text.contains("Branch{"));
        assertTrue(text.contains("id=null"));
        assertTrue(text.contains("name='Updated Branch'"));
        assertTrue(text.contains("address='New Address, 2'"));
    }
}