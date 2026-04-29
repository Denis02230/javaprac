package bankinfo.web.service;

import bankinfo.dao.AccountDao;
import bankinfo.dao.BranchDao;
import bankinfo.model.Account;
import bankinfo.model.Branch;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BranchService {

    private final BranchDao branchDao;
    private final AccountDao accountDao;

    public BranchService(BranchDao branchDao, AccountDao accountDao) {
        this.branchDao = branchDao;
        this.accountDao = accountDao;
    }

    public List<Branch> findBranches(String query) {
        if (query == null || query.isBlank()) {
            return branchDao.findAll();
        }
        return branchDao.findByNameContains(query.trim());
    }

    public Optional<Branch> findBranchDetails(Long id) {
        return branchDao.findByIdDetailed(id);
    }

    public Branch saveBranch(Long id, String name, String address) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Branch name is required");
        }
        if (address == null || address.isBlank()) {
            throw new ValidationException("Branch address is required");
        }

        Branch branch = id == null ? new Branch() : branchDao.findById(id)
                .orElseThrow(() -> new ValidationException("Branch not found: " + id));

        branch.setName(name.trim());
        branch.setAddress(address.trim());
        return branchDao.save(branch);
    }

    public void deleteBranch(Long id) {
        Branch branch = branchDao.findById(id)
                .orElseThrow(() -> new ValidationException("Branch not found: " + id));

        List<Account> accounts = accountDao.findByBranchId(branch.getId());
        boolean hasOpenAccounts = accounts.stream().anyMatch(a -> a.getClosedAt() == null);
        if (hasOpenAccounts) {
            throw new ValidationException("Cannot delete branch with open accounts");
        }

        branchDao.deleteById(id);
    }
}
