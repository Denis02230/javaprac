package bankinfo.dao;

import bankinfo.model.Branch;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

public class BranchDaoTest {

    private final BranchDao branchDao = new BranchDao();

    @BeforeMethod
    public void resetDatabase() {
        TestDbHelper.recreateAndFillDatabase();
    }

    @Test
    public void findAll_shouldReturnAllBranches() {
        List<Branch> branches = branchDao.findAll();

        assertNotNull(branches);
        assertEquals(branches.size(), 3);
        assertEquals(branches.get(0).getName(), "Central Branch");
        assertEquals(branches.get(1).getName(), "North Branch");
        assertEquals(branches.get(2).getName(), "South Branch");
    }

    @Test
    public void findById_shouldReturnBranch_whenBranchExists() {
        Optional<Branch> result = branchDao.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(result.get().getName(), "Central Branch");
        assertEquals(result.get().getAddress(), "Moscow, Mikluho-Maklaya st, 5");
    }

    @Test
    public void findById_shouldReturnEmpty_whenBranchDoesNotExist() {
        Optional<Branch> result = branchDao.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findByIdWithAccounts_shouldLoadAccounts() {
        Optional<Branch> result = branchDao.findByIdWithAccounts(1L);

        assertTrue(result.isPresent());

        Branch branch = result.get();
        assertEquals(branch.getName(), "Central Branch");
        assertEquals(branch.getAccounts().size(), 4);

        assertEquals(branch.getAccounts().get(0).getAccountNumber(), "ACC-0001");
        assertEquals(branch.getAccounts().get(1).getAccountNumber(), "ACC-0002");
        assertEquals(branch.getAccounts().get(2).getAccountNumber(), "ACC-0004");
        assertEquals(branch.getAccounts().get(3).getAccountNumber(), "ACC-0005");
    }

    @Test
    public void findByIdWithAccounts_shouldReturnEmpty_whenBranchDoesNotExist() {
        Optional<Branch> result = branchDao.findByIdWithAccounts(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findByNameContains_shouldReturnMatches_caseInsensitive() {
        List<Branch> branches = branchDao.findByNameContains("branch");

        assertEquals(branches.size(), 3);
    }

    @Test
    public void findByNameContains_shouldReturnOnlyMatchingRows() {
        List<Branch> branches = branchDao.findByNameContains("north");

        assertEquals(branches.size(), 1);
        assertEquals(branches.get(0).getName(), "North Branch");
    }

    @Test
    public void findByNameContains_shouldReturnEmpty_whenNoMatches() {
        List<Branch> branches = branchDao.findByNameContains("zzz");

        assertNotNull(branches);
        assertTrue(branches.isEmpty());
    }

    @Test
    public void findByIdDetailed_shouldLoadAccountsAndLinkedEntities() {
        Optional<Branch> result = branchDao.findByIdDetailed(1L);

        assertTrue(result.isPresent());
        Branch branch = result.get();

        assertEquals(branch.getName(), "Central Branch");
        assertEquals(branch.getAccounts().size(), 4);
        assertTrue(branch.getAccounts().stream().allMatch(a -> a.getClient() != null));
        assertTrue(branch.getAccounts().stream().allMatch(a -> a.getAccountType() != null));
    }

    @Test
    public void findByIdDetailed_shouldReturnEmpty_whenBranchDoesNotExist() {
        Optional<Branch> result = branchDao.findByIdDetailed(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findBranchesWithOpenAccounts_shouldReturnAllBranchesThatHaveOpenAccounts() {
        List<Branch> branches = branchDao.findBranchesWithOpenAccounts();

        assertEquals(branches.size(), 3);
        assertEquals(branches.get(0).getName(), "Central Branch");
        assertEquals(branches.get(1).getName(), "North Branch");
        assertEquals(branches.get(2).getName(), "South Branch");
    }

    @Test
    public void save_shouldInsertNewBranch() {
        Branch branch = new Branch(
                "West Branch",
                "Moscow, West street, 10"
        );

        Branch saved = branchDao.save(branch);

        assertNotNull(saved.getId());

        Optional<Branch> loaded = branchDao.findById(saved.getId());
        assertTrue(loaded.isPresent());
        assertEquals(loaded.get().getName(), "West Branch");
        assertEquals(loaded.get().getAddress(), "Moscow, West street, 10");
    }

    @Test
    public void deleteById_shouldDeleteExistingBranch() {
        Branch transientBranch = new Branch(
                "Temp Removable Branch",
                "Moscow, Temporary st, 99"
        );
        Branch persisted = branchDao.save(transientBranch);

        Optional<Branch> before = branchDao.findById(persisted.getId());
        assertTrue(before.isPresent());

        branchDao.deleteById(persisted.getId());

        Optional<Branch> after = branchDao.findById(persisted.getId());
        assertTrue(after.isEmpty());
    }

    @Test
    public void deleteById_shouldDoNothing_whenBranchMissing() {
        branchDao.deleteById(999L);

        List<Branch> branches = branchDao.findAll();
        assertEquals(branches.size(), 3);
    }
}
