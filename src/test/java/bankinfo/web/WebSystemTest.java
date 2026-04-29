package bankinfo.web;

import bankinfo.dao.AccountDao;
import bankinfo.dao.AccountTxDao;
import bankinfo.dao.AccountTypeDao;
import bankinfo.dao.BranchDao;
import bankinfo.dao.ClientDao;
import bankinfo.dao.TestDbHelper;
import bankinfo.model.Account;
import bankinfo.model.AccountStatus;
import bankinfo.model.AccountTx;
import bankinfo.model.AccountType;
import bankinfo.model.Client;
import bankinfo.model.ClientType;
import bankinfo.model.TxType;
import bankinfo.web.service.InterestRunResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@ContextConfiguration(locations = "classpath:spring/bankinfo-mvc.xml")
@WebAppConfiguration("src/main/webapp")
public class WebSystemTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BranchDao branchDao;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private AccountTxDao accountTxDao;

    @Autowired
    private AccountTypeDao accountTypeDao;

    private MockMvc mockMvc;

    @BeforeMethod
    public void setUp() {
        TestDbHelper.recreateAndFillDatabase();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void homePage_shouldRenderMainNavigation() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("p1-home"));
    }

    @Test
    public void pageNavigation_shouldOpenAllMainSections() throws Exception {
        mockMvc.perform(get("/branches"))
                .andExpect(status().isOk())
                .andExpect(view().name("p2-branches"));

        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(view().name("p5-clients"));

        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(view().name("p8-accounts"));

        mockMvc.perform(get("/operations"))
                .andExpect(status().isOk())
                .andExpect(view().name("p13-operations"));

        mockMvc.perform(get("/account-types"))
                .andExpect(status().isOk())
                .andExpect(view().name("p14-account-types"));

        mockMvc.perform(get("/interest"))
                .andExpect(status().isOk())
                .andExpect(view().name("p17-interest"));
    }

    @Test
    public void forms_shouldBeAccessibleForMainEditFlows() throws Exception {
        mockMvc.perform(get("/branches/form"))
                .andExpect(status().isOk())
                .andExpect(view().name("p4-branch-form"));

        mockMvc.perform(get("/branches/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("p4-branch-form"));

        mockMvc.perform(get("/clients/form"))
                .andExpect(status().isOk())
                .andExpect(view().name("p7-client-form"));

        mockMvc.perform(get("/clients/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("p7-client-form"));

        mockMvc.perform(get("/accounts/open"))
                .andExpect(status().isOk())
                .andExpect(view().name("p10-open-account"));

        mockMvc.perform(get("/accounts/1/close"))
                .andExpect(status().isOk())
                .andExpect(view().name("p11-close-account"));

        mockMvc.perform(get("/accounts/1/tx").param("type", "DEBIT"))
                .andExpect(status().isOk())
                .andExpect(view().name("p12-account-operation"));

        mockMvc.perform(get("/account-types/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("p16-account-type-form"));
    }

    @Test
    public void accountTxForm_shouldUseDefaultType_whenNotProvided() throws Exception {
        mockMvc.perform(get("/accounts/1/tx"))
                .andExpect(status().isOk())
                .andExpect(view().name("p12-account-operation"))
                .andExpect(model().attributeExists("form"));
    }

    @Test
    public void cards_shouldBeAccessibleForNavigationPaths() throws Exception {
        mockMvc.perform(get("/branches/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("p3-branch"));

        mockMvc.perform(get("/clients/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("p6-client"));

        mockMvc.perform(get("/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("p9-account"));

        mockMvc.perform(get("/account-types/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("p15-account-type"));
    }

    @Test
    public void cards_shouldRenderValidationErrorPage_whenEntityNotFound() throws Exception {
        mockMvc.perform(get("/branches/999999"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorTitle", "Validation error"));

        mockMvc.perform(get("/clients/999999"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorTitle", "Validation error"));

        mockMvc.perform(get("/accounts/999999"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorTitle", "Validation error"));

        mockMvc.perform(get("/account-types/999999"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorTitle", "Validation error"));
    }

    @Test
    public void editForms_shouldRenderValidationErrorPage_whenEntityNotFound() throws Exception {
        mockMvc.perform(get("/branches/999999/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorTitle", "Validation error"));

        mockMvc.perform(get("/clients/999999/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorTitle", "Validation error"));

        mockMvc.perform(get("/accounts/999999/close"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorTitle", "Validation error"));

        mockMvc.perform(get("/accounts/999999/tx").param("type", "DEBIT"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorTitle", "Validation error"));

        mockMvc.perform(get("/account-types/999999/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorTitle", "Validation error"));
    }

    @Test
    public void branchesPage_shouldSupportSearch() throws Exception {
        mockMvc.perform(get("/branches").param("q", "Central"))
                .andExpect(status().isOk())
                .andExpect(view().name("p2-branches"))
                .andExpect(model().attributeExists("branches"));
    }

    @Test
    public void clientsPage_shouldSupportTypeAndNameFilters() throws Exception {
        mockMvc.perform(get("/clients")
                        .param("type", "ORG")
                        .param("q", "alpha"))
                .andExpect(status().isOk())
                .andExpect(view().name("p5-clients"))
                .andExpect(model().attribute("selectedType", ClientType.ORG))
                .andExpect(model().attribute("clients", hasSize(1)));
    }

    @Test
    public void clientsPage_shouldSupportNameFilterOnly() throws Exception {
        mockMvc.perform(get("/clients").param("q", "Anna"))
                .andExpect(status().isOk())
                .andExpect(view().name("p5-clients"))
                .andExpect(model().attribute("clients", hasSize(1)));
    }

    @Test
    public void clientsPage_shouldSupportTypeFilterOnly() throws Exception {
        mockMvc.perform(get("/clients").param("type", "ORG"))
                .andExpect(status().isOk())
                .andExpect(view().name("p5-clients"))
                .andExpect(model().attribute("selectedType", ClientType.ORG))
                .andExpect(model().attribute("clients", hasSize(3)));
    }

    @Test
    public void accountsPage_shouldSupportStatusAndOwnerFilters() throws Exception {
        mockMvc.perform(get("/accounts")
                        .param("status", "CLOSED")
                        .param("clientId", "2")
                        .param("branchId", "2")
                        .param("accountTypeId", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("p8-accounts"))
                .andExpect(model().attribute("selectedStatus", AccountStatus.CLOSED))
                .andExpect(model().attribute("accounts", hasSize(1)));
    }

    @Test
    public void accountsPage_shouldSupportAccountNumberFilterOnly() throws Exception {
        mockMvc.perform(get("/accounts").param("q", "ACC-0001"))
                .andExpect(status().isOk())
                .andExpect(view().name("p8-accounts"))
                .andExpect(model().attribute("accounts", hasSize(1)));
    }

    @Test
    public void accountsPage_shouldSupportStatusFilterOnly() throws Exception {
        mockMvc.perform(get("/accounts").param("status", "CLOSED"))
                .andExpect(status().isOk())
                .andExpect(view().name("p8-accounts"))
                .andExpect(model().attribute("selectedStatus", AccountStatus.CLOSED))
                .andExpect(model().attribute("accounts", hasSize(2)));
    }

    @Test
    public void accountsPage_shouldSupportClientFilterOnly() throws Exception {
        mockMvc.perform(get("/accounts").param("clientId", "4"))
                .andExpect(status().isOk())
                .andExpect(view().name("p8-accounts"))
                .andExpect(model().attribute("accounts", hasSize(2)));
    }

    @Test
    public void accountsPage_shouldSupportBranchFilterOnly() throws Exception {
        mockMvc.perform(get("/accounts").param("branchId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("p8-accounts"))
                .andExpect(model().attribute("accounts", hasSize(4)));
    }

    @Test
    public void accountsPage_shouldSupportAccountTypeFilterOnly() throws Exception {
        mockMvc.perform(get("/accounts").param("accountTypeId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("p8-accounts"))
                .andExpect(model().attribute("accounts", hasSize(2)));
    }

    @Test
    public void accountTypesPage_shouldSupportSearch() throws Exception {
        mockMvc.perform(get("/account-types").param("q", "Savings"))
                .andExpect(status().isOk())
                .andExpect(view().name("p14-account-types"))
                .andExpect(model().attribute("accountTypes", hasSize(1)));
    }

    @Test
    public void saveBranch_shouldCreateNewBranch() throws Exception {
        mockMvc.perform(post("/branches/save")
                        .param("name", "Web Test Branch")
                        .param("address", "Moscow, Test st, 99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/branches/*"));

        assertFalse(branchDao.findByNameContains("Web Test Branch").isEmpty());
    }

    @Test
    public void saveBranch_shouldRenderFormError_whenNameMissing() throws Exception {
        mockMvc.perform(post("/branches/save")
                        .param("name", "")
                        .param("address", "Address"))
                .andExpect(status().isOk())
                .andExpect(view().name("p4-branch-form"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    public void saveBranch_shouldRenderFormError_whenAddressMissing() throws Exception {
        mockMvc.perform(post("/branches/save")
                        .param("name", "Branch without address")
                        .param("address", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("p4-branch-form"))
                .andExpect(model().attribute("errorMessage", "Branch address is required"));
    }

    @Test
    public void editBranch_shouldUpdateExistingBranch() throws Exception {
        mockMvc.perform(post("/branches/save")
                        .param("id", "1")
                        .param("name", "Central Branch Updated")
                        .param("address", "Moscow, Updated"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/branches/1"));

        Optional<bankinfo.model.Branch> branch = branchDao.findById(1L);
        assertTrue(branch.isPresent());
        assertEquals(branch.get().getName(), "Central Branch Updated");
        assertEquals(branch.get().getAddress(), "Moscow, Updated");
    }

    @Test
    public void deleteBranch_shouldFailWhenOpenAccountsExist() throws Exception {
        mockMvc.perform(post("/branches/1/delete"))
                .andExpect(status().isOk())
                .andExpect(view().name("p3-branch"))
                .andExpect(model().attributeExists("errorMessage"));

        assertTrue(branchDao.findById(1L).isPresent());
    }

    @Test
    public void deleteBranch_shouldSucceedWithoutAccounts() throws Exception {
        mockMvc.perform(post("/branches/save")
                        .param("name", "Delete Me Branch")
                        .param("address", "No account address"))
                .andExpect(status().is3xxRedirection());

        Long createdId = branchDao.findByNameContains("Delete Me Branch").get(0).getId();

        mockMvc.perform(post("/branches/" + createdId + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/branches"));

        assertTrue(branchDao.findById(createdId).isEmpty());
    }

    @Test
    public void saveBranch_shouldRenderFormError_whenEditedBranchNotFound() throws Exception {
        mockMvc.perform(post("/branches/save")
                        .param("id", "999999")
                        .param("name", "Ghost branch")
                        .param("address", "Ghost address"))
                .andExpect(status().isOk())
                .andExpect(view().name("p4-branch-form"))
                .andExpect(model().attribute("errorMessage", "Branch not found: 999999"));
    }

    @Test
    public void deleteBranch_shouldRenderValidationErrorPage_whenBranchNotFound() throws Exception {
        mockMvc.perform(post("/branches/999999/delete"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorTitle", "Validation error"))
                .andExpect(model().attribute("errorMessage", "Branch not found: 999999"));
    }

    @Test
    public void saveClient_shouldCreateNewClient() throws Exception {
        int beforeCount = clientDao.findAll().size();

        mockMvc.perform(post("/clients/save")
                        .param("clientType", "PERSON")
                        .param("displayName", "Web Client"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/clients/*"));

        assertEquals(clientDao.findAll().size(), beforeCount + 1);
    }

    @Test
    public void saveClient_shouldRenderFormError_whenDisplayNameMissing() throws Exception {
        mockMvc.perform(post("/clients/save")
                        .param("clientType", "PERSON")
                        .param("displayName", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("p7-client-form"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    public void saveClient_shouldRenderFormError_whenTypeMissing() throws Exception {
        mockMvc.perform(post("/clients/save")
                        .param("displayName", "No type client"))
                .andExpect(status().isOk())
                .andExpect(view().name("p7-client-form"))
                .andExpect(model().attribute("errorMessage", "Client type is required"));
    }

    @Test
    public void editClient_shouldUpdateDisplayName() throws Exception {
        mockMvc.perform(post("/clients/save")
                        .param("id", "1")
                        .param("clientType", "PERSON")
                        .param("displayName", "Ivan Petrov Updated"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/clients/1"));

        Optional<Client> client = clientDao.findById(1L);
        assertTrue(client.isPresent());
        assertEquals(client.get().getDisplayName(), "Ivan Petrov Updated");
    }

    @Test
    public void deleteClient_shouldFailWhenOpenAccountsExist() throws Exception {
        mockMvc.perform(post("/clients/1/delete"))
                .andExpect(status().isOk())
                .andExpect(view().name("p6-client"))
                .andExpect(model().attributeExists("errorMessage"));

        assertTrue(clientDao.findById(1L).isPresent());
    }

    @Test
    public void deleteClient_shouldSucceedWhenOnlyClosedAccountsExist() throws Exception {
        mockMvc.perform(post("/clients/save")
                        .param("clientType", "PERSON")
                        .param("displayName", "Delete Me Client"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/clients/*"));

        Long clientId = clientDao.findByDisplayNameContains("Delete Me Client").get(0).getId();

        mockMvc.perform(post("/clients/" + clientId + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/clients"));

        assertTrue(clientDao.findById(clientId).isEmpty());
    }

    @Test
    public void saveClient_shouldRenderFormError_whenEditedClientNotFound() throws Exception {
        mockMvc.perform(post("/clients/save")
                        .param("id", "999999")
                        .param("clientType", "PERSON")
                        .param("displayName", "Ghost client"))
                .andExpect(status().isOk())
                .andExpect(view().name("p7-client-form"))
                .andExpect(model().attribute("errorMessage", "Client not found: 999999"));
    }

    @Test
    public void deleteClient_shouldRenderValidationErrorPage_whenClientNotFound() throws Exception {
        mockMvc.perform(post("/clients/999999/delete"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorTitle", "Validation error"))
                .andExpect(model().attribute("errorMessage", "Client not found: 999999"));
    }

    @Test
    public void openAccount_shouldCreateAccountAndRedirectToCard() throws Exception {
        int beforeCount = accountDao.findAll().size();

        mockMvc.perform(post("/accounts/open")
                        .param("clientId", "1")
                        .param("branchId", "1")
                        .param("accountTypeId", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/accounts/*"));

        int afterCount = accountDao.findAll().size();
        assertEquals(afterCount, beforeCount + 1);
    }

    @Test
    public void openAccount_shouldRenderFormError_whenRequiredFieldMissing() throws Exception {
        mockMvc.perform(post("/accounts/open")
                        .param("branchId", "1")
                        .param("accountTypeId", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("p10-open-account"))
                .andExpect(model().attribute("errorMessage", "Client is required"));
    }

    @Test
    public void openAccount_shouldRenderFormError_whenBranchMissing() throws Exception {
        mockMvc.perform(post("/accounts/open")
                        .param("clientId", "1")
                        .param("accountTypeId", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("p10-open-account"))
                .andExpect(model().attribute("errorMessage", "Branch is required"));
    }

    @Test
    public void openAccount_shouldRenderFormError_whenAccountTypeMissing() throws Exception {
        mockMvc.perform(post("/accounts/open")
                        .param("clientId", "1")
                        .param("branchId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("p10-open-account"))
                .andExpect(model().attribute("errorMessage", "Account type is required"));
    }

    @Test
    public void openAccount_shouldRenderFormError_whenForeignEntityNotFound() throws Exception {
        mockMvc.perform(post("/accounts/open")
                        .param("clientId", "999999")
                        .param("branchId", "1")
                        .param("accountTypeId", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("p10-open-account"))
                .andExpect(model().attribute("errorMessage", "Client not found: 999999"));

        mockMvc.perform(post("/accounts/open")
                        .param("clientId", "1")
                        .param("branchId", "999999")
                        .param("accountTypeId", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("p10-open-account"))
                .andExpect(model().attribute("errorMessage", "Branch not found: 999999"));

        mockMvc.perform(post("/accounts/open")
                        .param("clientId", "1")
                        .param("branchId", "1")
                        .param("accountTypeId", "999999"))
                .andExpect(status().isOk())
                .andExpect(view().name("p10-open-account"))
                .andExpect(model().attribute("errorMessage", "Account type not found: 999999"));
    }

    @Test
    public void closeAccount_shouldRejectNonZeroBalance() throws Exception {
        mockMvc.perform(post("/accounts/1/close"))
                .andExpect(status().isOk())
                .andExpect(view().name("p11-close-account"))
                .andExpect(model().attributeExists("errorMessage"));

        Optional<Account> account = accountDao.findById(1L);
        assertTrue(account.isPresent());
        assertTrue(account.get().getClosedAt() == null);
    }

    @Test
    public void closeAccount_shouldCloseZeroBalanceAccount() throws Exception {
        mockMvc.perform(post("/accounts/open")
                        .param("clientId", "2")
                        .param("branchId", "2")
                        .param("accountTypeId", "2"))
                .andExpect(status().is3xxRedirection());

        List<Account> accounts = accountDao.findAll();
        Account newAccount = accounts.get(accounts.size() - 1);

        mockMvc.perform(post("/accounts/" + newAccount.getId() + "/close"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/accounts/" + newAccount.getId()));

        Optional<Account> closed = accountDao.findById(newAccount.getId());
        assertTrue(closed.isPresent());
        assertTrue(closed.get().getClosedAt() != null);
    }

    @Test
    public void closeAccount_shouldRejectAlreadyClosedAccount() throws Exception {
        mockMvc.perform(post("/accounts/7/close"))
                .andExpect(status().isOk())
                .andExpect(view().name("p11-close-account"))
                .andExpect(model().attribute("errorMessage", "Account is already closed"));
    }

    @Test
    public void closeAccount_shouldRenderValidationErrorPage_whenAccountNotFound() throws Exception {
        mockMvc.perform(post("/accounts/999999/close"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorTitle", "Validation error"))
                .andExpect(model().attribute("errorMessage", "Account not found: 999999"));
    }

    @Test
    public void debit_shouldRejectWhenMaxCreditExceeded() throws Exception {
        mockMvc.perform(post("/accounts/1/tx")
                        .param("txType", "DEBIT")
                        .param("amount", "2000")
                        .param("description", "too much"))
                .andExpect(status().isOk())
                .andExpect(view().name("p12-account-operation"))
                .andExpect(model().attributeExists("errorMessage"));

        Optional<Account> account = accountDao.findById(1L);
        assertTrue(account.isPresent());
        assertEquals(account.get().getBalance(), new BigDecimal("1200.00"));
    }

    @Test
    public void transaction_shouldRejectInvalidInputAmount() throws Exception {
        int txBefore = accountTxDao.findByAccountId(1L).size();

        mockMvc.perform(post("/accounts/1/tx")
                        .param("txType", "CREDIT")
                        .param("amount", "-1")
                        .param("description", "bad"))
                .andExpect(status().isOk())
                .andExpect(view().name("p12-account-operation"))
                .andExpect(model().attributeExists("errorMessage"));

        int txAfter = accountTxDao.findByAccountId(1L).size();
        assertEquals(txAfter, txBefore);
    }

    @Test
    public void transaction_shouldRejectWhenTypeMissing() throws Exception {
        mockMvc.perform(post("/accounts/1/tx")
                        .param("amount", "100"))
                .andExpect(status().isOk())
                .andExpect(view().name("p12-account-operation"))
                .andExpect(model().attribute("errorMessage", "Transaction type is required"));
    }

    @Test
    public void transaction_shouldRejectWhenAccountClosed() throws Exception {
        mockMvc.perform(post("/accounts/7/tx")
                        .param("txType", "CREDIT")
                        .param("amount", "100")
                        .param("description", "closed account"))
                .andExpect(status().isOk())
                .andExpect(view().name("p12-account-operation"))
                .andExpect(model().attribute("errorMessage", "Transaction is not allowed for closed account"));
    }

    @Test
    public void transaction_shouldRenderValidationErrorPage_whenAccountNotFound() throws Exception {
        mockMvc.perform(post("/accounts/999999/tx")
                        .param("txType", "CREDIT")
                        .param("amount", "100"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorTitle", "Validation error"))
                .andExpect(model().attribute("errorMessage", "Account not found: 999999"));
    }

    @Test
    public void debit_shouldRejectWhenOperationNotAllowedByAccountType() throws Exception {
        mockMvc.perform(post("/accounts/5/tx")
                        .param("txType", "DEBIT")
                        .param("amount", "100")
                        .param("description", "forbidden debit"))
                .andExpect(status().isOk())
                .andExpect(view().name("p12-account-operation"))
                .andExpect(model().attribute("errorMessage", "Debit operation is not allowed for account type Deposit To Other"));
    }

    @Test
    public void credit_shouldRejectByMinAndMaxLimits() throws Exception {
        mockMvc.perform(post("/accounts/2/tx")
                        .param("txType", "CREDIT")
                        .param("amount", "5")
                        .param("description", "below min"))
                .andExpect(status().isOk())
                .andExpect(view().name("p12-account-operation"))
                .andExpect(model().attribute("errorMessage", "Credit amount must be >= 10.00"));

        mockMvc.perform(post("/accounts/2/tx")
                        .param("txType", "CREDIT")
                        .param("amount", "60000")
                        .param("description", "above max"))
                .andExpect(status().isOk())
                .andExpect(view().name("p12-account-operation"))
                .andExpect(model().attribute("errorMessage", "Credit amount must be <= 50000.00"));
    }

    @Test
    public void debit_shouldRejectByMinAndMaxLimits() throws Exception {
        mockMvc.perform(post("/accounts/2/tx")
                        .param("txType", "DEBIT")
                        .param("amount", "5")
                        .param("description", "below min"))
                .andExpect(status().isOk())
                .andExpect(view().name("p12-account-operation"))
                .andExpect(model().attribute("errorMessage", "Debit amount must be >= 10.00"));

        mockMvc.perform(post("/accounts/2/tx")
                        .param("txType", "DEBIT")
                        .param("amount", "6000")
                        .param("description", "above max"))
                .andExpect(status().isOk())
                .andExpect(view().name("p12-account-operation"))
                .andExpect(model().attribute("errorMessage", "Debit amount must be <= 5000.00"));
    }

    @Test
    public void debit_shouldUpdateBalanceAndCreateOperation() throws Exception {
        int txBefore = accountTxDao.findByAccountId(1L).size();

        mockMvc.perform(post("/accounts/1/tx")
                        .param("txType", "DEBIT")
                        .param("amount", "100")
                        .param("description", "web debit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/accounts/1"));

        Optional<Account> account = accountDao.findById(1L);
        assertTrue(account.isPresent());
        assertEquals(account.get().getBalance(), new BigDecimal("1100.00"));
        assertEquals(accountTxDao.findByAccountId(1L).size(), txBefore + 1);
    }

    @Test
    public void credit_shouldRejectWhenCreditDisabledByAccountType() throws Exception {
        AccountType accountType = accountTypeDao.findById(2L).orElseThrow();
        accountType.setAllowCredit(false);
        accountTypeDao.save(accountType);

        mockMvc.perform(post("/accounts/1/tx")
                        .param("txType", "CREDIT")
                        .param("amount", "100")
                        .param("description", "credit disabled"))
                .andExpect(status().isOk())
                .andExpect(view().name("p12-account-operation"))
                .andExpect(model().attribute("errorMessage", "Credit operation is not allowed for account type Checking Standard"));
    }

    @Test
    public void credit_shouldUpdateBalanceAndCreateOperation() throws Exception {
        int txBefore = accountTxDao.findByAccountId(1L).size();

        mockMvc.perform(post("/accounts/1/tx")
                        .param("txType", "CREDIT")
                        .param("amount", "100")
                        .param("description", "web credit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/accounts/1"));

        Optional<Account> account = accountDao.findById(1L);
        assertTrue(account.isPresent());
        assertEquals(account.get().getBalance(), new BigDecimal("1300.00"));

        List<AccountTx> txAfter = accountTxDao.findByAccountId(1L);
        assertEquals(txAfter.size(), txBefore + 1);
    }

    @Test
    public void accountCard_shouldShowValidationError_whenPeriodFormatInvalid() throws Exception {
        mockMvc.perform(get("/accounts/1")
                        .param("from", "bad-date")
                        .param("to", "2026-03-01T00:00:00+00:00"))
                .andExpect(status().isOk())
                .andExpect(view().name("p9-account"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    public void accountCard_shouldShowValidationError_whenPeriodRangeInvalid() throws Exception {
        mockMvc.perform(get("/accounts/1")
                        .param("from", "2026-03-02T00:00:00+00:00")
                        .param("to", "2026-03-01T00:00:00+00:00"))
                .andExpect(status().isOk())
                .andExpect(view().name("p9-account"))
                .andExpect(model().attribute("errorMessage", "Period start must be before or equal to period end"));
    }

    @Test
    public void accountCard_shouldFilterTransactions_whenPeriodValid() throws Exception {
        mockMvc.perform(get("/accounts/1")
                        .param("from", "2026-02-07T00:00:00+00:00")
                        .param("to", "2026-02-07T23:59:59+00:00"))
                .andExpect(status().isOk())
                .andExpect(view().name("p9-account"))
                .andExpect(model().attribute("transactions", hasSize(1)));
    }

    @Test
    public void operationsPage_shouldApplyFilters() throws Exception {
        mockMvc.perform(get("/operations")
                        .param("type", "INTEREST")
                        .param("accountId", "4"))
                .andExpect(status().isOk())
                .andExpect(view().name("p13-operations"))
                .andExpect(model().attributeExists("operations"));
    }

    @Test
    public void operationsPage_shouldFilterByValidPeriod() throws Exception {
        mockMvc.perform(get("/operations")
                        .param("from", "2026-02-06T10:00:30+00:00")
                        .param("to", "2026-02-06T10:01:30+00:00"))
                .andExpect(status().isOk())
                .andExpect(view().name("p13-operations"))
                .andExpect(model().attribute("operations", hasSize(1)));
    }

    @Test
    public void operationsPage_shouldFilterByTypeOnly() throws Exception {
        mockMvc.perform(get("/operations").param("type", "INTEREST"))
                .andExpect(status().isOk())
                .andExpect(view().name("p13-operations"))
                .andExpect(model().attribute("selectedType", TxType.INTEREST))
                .andExpect(model().attribute("operations", hasSize(5)));
    }

    @Test
    public void operationsPage_shouldFilterByAccountOnly() throws Exception {
        mockMvc.perform(get("/operations").param("accountId", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("p13-operations"))
                .andExpect(model().attribute("operations", hasSize(1)));
    }

    @Test
    public void operationsPage_shouldFilterByClientOnly() throws Exception {
        mockMvc.perform(get("/operations").param("clientId", "4"))
                .andExpect(status().isOk())
                .andExpect(view().name("p13-operations"))
                .andExpect(model().attribute("operations", hasSize(7)));
    }

    @Test
    public void operationsPage_shouldFilterByBranchOnly() throws Exception {
        mockMvc.perform(get("/operations").param("branchId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("p13-operations"))
                .andExpect(model().attribute("operations", hasSize(13)));
    }

    @Test
    public void operationsPage_shouldFilterByAccountTypeOnly() throws Exception {
        mockMvc.perform(get("/operations").param("accountTypeId", "4"))
                .andExpect(status().isOk())
                .andExpect(view().name("p13-operations"))
                .andExpect(model().attribute("operations", hasSize(1)));
    }

    @Test
    public void operationsPage_shouldApplyClientBranchAndAccountTypeFilters() throws Exception {
        mockMvc.perform(get("/operations")
                        .param("type", "CREDIT")
                        .param("clientId", "4")
                        .param("branchId", "1")
                        .param("accountTypeId", "4"))
                .andExpect(status().isOk())
                .andExpect(view().name("p13-operations"))
                .andExpect(model().attribute("selectedType", TxType.CREDIT))
                .andExpect(model().attribute("operations", hasSize(1)));
    }

    @Test
    public void operationsPage_shouldShowValidationError_whenPeriodInvalid() throws Exception {
        mockMvc.perform(get("/operations")
                        .param("from", "2026-03-02T00:00:00+00:00")
                        .param("to", "2026-03-01T00:00:00+00:00"))
                .andExpect(status().isOk())
                .andExpect(view().name("p13-operations"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    public void operationsPage_shouldShowValidationError_whenPeriodFormatInvalid() throws Exception {
        mockMvc.perform(get("/operations")
                        .param("from", "bad-date")
                        .param("to", "2026-03-01T00:00:00+00:00"))
                .andExpect(status().isOk())
                .andExpect(view().name("p13-operations"))
                .andExpect(model().attribute("errorMessage", "Invalid date-time format. Expected ISO offset date-time"));
    }

    @Test
    public void accountTypeSave_shouldUpdateAccountType() throws Exception {
        mockMvc.perform(post("/account-types/1/save")
                        .param("name", "Savings Updated")
                        .param("maxCredit", "0")
                        .param("creditRepayRule", "No credit")
                        .param("interestRate", "0.0600")
                        .param("interestInterval", "MONTHLY")
                        .param("interestMethod", "TO_SAME_ACCOUNT")
                        .param("allowDebit", "true")
                        .param("allowCredit", "true")
                        .param("minCreditAmount", "10")
                        .param("maxCreditAmount", "50000")
                        .param("minDebitAmount", "10")
                        .param("maxDebitAmount", "5000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/account-types/1"));

        Optional<AccountType> accountType = accountTypeDao.findById(1L);
        assertTrue(accountType.isPresent());
        assertEquals(accountType.get().getName(), "Savings Updated");
        assertEquals(accountType.get().getInterestRate(), new BigDecimal("0.0600"));
    }

    @Test
    public void accountTypeSave_shouldPersistDailyIntervalAndOtherAccountMethod() throws Exception {
        mockMvc.perform(post("/account-types/4/save")
                        .param("name", "Deposit To Other Updated")
                        .param("maxCredit", "0")
                        .param("creditRepayRule", "Deposit rules")
                        .param("interestRate", "0.0300")
                        .param("interestInterval", "DAILY")
                        .param("interestMethod", "TO_OTHER_ACCOUNT")
                        .param("allowDebit", "false")
                        .param("allowCredit", "true")
                        .param("minCreditAmount", "100")
                        .param("maxCreditAmount", "1000000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/account-types/4"));

        AccountType accountType = accountTypeDao.findById(4L).orElseThrow();
        assertEquals(accountType.getInterestInterval().name(), "DAILY");
        assertEquals(accountType.getInterestMethod().name(), "TO_OTHER_ACCOUNT");
    }

    @Test
    public void accountTypeSave_shouldPersistQuarterlyIntervalAndDisabledCredit() throws Exception {
        mockMvc.perform(post("/account-types/3/save")
                        .param("name", "Overdraft Updated")
                        .param("maxCredit", "1000")
                        .param("creditRepayRule", "Overdraft rules")
                        .param("interestRate", "0.0100")
                        .param("interestInterval", "QUARTERLY")
                        .param("interestMethod", "TO_SAME_ACCOUNT")
                        .param("allowDebit", "true")
                        .param("allowCredit", "false")
                        .param("minCreditAmount", "1")
                        .param("maxCreditAmount", "500000")
                        .param("minDebitAmount", "1")
                        .param("maxDebitAmount", "500000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/account-types/3"));

        AccountType accountType = accountTypeDao.findById(3L).orElseThrow();
        assertEquals(accountType.getInterestInterval().name(), "QUARTERLY");
        assertFalse(accountType.getAllowCredit());
    }

    @Test
    public void accountTypeSave_shouldPersistYearlyIntervalAndZeroInterestRate() throws Exception {
        mockMvc.perform(post("/account-types/2/save")
                        .param("name", "Checking Updated")
                        .param("maxCredit", "0")
                        .param("creditRepayRule", "No credit")
                        .param("interestRate", "0")
                        .param("interestInterval", "YEARLY")
                        .param("interestMethod", "TO_SAME_ACCOUNT")
                        .param("allowDebit", "true")
                        .param("allowCredit", "true")
                        .param("minCreditAmount", "1")
                        .param("maxCreditAmount", "200000")
                        .param("minDebitAmount", "1")
                        .param("maxDebitAmount", "200000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/account-types/2"));

        AccountType accountType = accountTypeDao.findById(2L).orElseThrow();
        assertEquals(accountType.getInterestInterval().name(), "YEARLY");
        assertEquals(accountType.getInterestRate().compareTo(BigDecimal.ZERO), 0);
    }

    @Test
    public void accountTypeSave_shouldRenderFormError_whenNameMissing() throws Exception {
        mockMvc.perform(post("/account-types/1/save")
                        .param("name", "")
                        .param("maxCredit", "0")
                        .param("creditRepayRule", "No credit")
                        .param("interestRate", "0.0500")
                        .param("interestInterval", "MONTHLY")
                        .param("interestMethod", "TO_SAME_ACCOUNT")
                        .param("allowDebit", "true")
                        .param("allowCredit", "true")
                        .param("minCreditAmount", "10")
                        .param("maxCreditAmount", "50000")
                        .param("minDebitAmount", "10")
                        .param("maxDebitAmount", "5000"))
                .andExpect(status().isOk())
                .andExpect(view().name("p16-account-type-form"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    public void accountTypeSave_shouldRenderFormError_whenAllowFlagsMissing() throws Exception {
        mockMvc.perform(post("/account-types/1/save")
                        .param("name", "Savings Basic")
                        .param("maxCredit", "0")
                        .param("creditRepayRule", "No credit")
                        .param("interestRate", "0.0500")
                        .param("interestInterval", "MONTHLY")
                        .param("interestMethod", "TO_SAME_ACCOUNT")
                        .param("minCreditAmount", "10")
                        .param("maxCreditAmount", "50000")
                        .param("minDebitAmount", "10")
                        .param("maxDebitAmount", "5000"))
                .andExpect(status().isOk())
                .andExpect(view().name("p16-account-type-form"))
                .andExpect(model().attribute("errorMessage", "Allow flags are required"));
    }

    @Test
    public void accountTypeSave_shouldRenderFormError_whenNumericValuesInvalid() throws Exception {
        mockMvc.perform(post("/account-types/1/save")
                        .param("name", "Savings Basic")
                        .param("maxCredit", "0")
                        .param("creditRepayRule", "No credit")
                        .param("interestRate", "-0.0100")
                        .param("interestInterval", "MONTHLY")
                        .param("interestMethod", "TO_SAME_ACCOUNT")
                        .param("allowDebit", "true")
                        .param("allowCredit", "true")
                        .param("minCreditAmount", "10")
                        .param("maxCreditAmount", "50000")
                        .param("minDebitAmount", "10")
                        .param("maxDebitAmount", "5000"))
                .andExpect(status().isOk())
                .andExpect(view().name("p16-account-type-form"))
                .andExpect(model().attribute("errorMessage", "Interest rate must be non-negative"));
    }

    @Test
    public void accountTypeSave_shouldRenderFormError_whenAccountTypeNotFound() throws Exception {
        mockMvc.perform(post("/account-types/999999/save")
                        .param("name", "Ghost type")
                        .param("maxCredit", "0")
                        .param("creditRepayRule", "No credit")
                        .param("interestRate", "0.0500")
                        .param("interestInterval", "MONTHLY")
                        .param("interestMethod", "TO_SAME_ACCOUNT")
                        .param("allowDebit", "true")
                        .param("allowCredit", "true")
                        .param("minCreditAmount", "10")
                        .param("maxCreditAmount", "50000")
                        .param("minDebitAmount", "10")
                        .param("maxDebitAmount", "5000"))
                .andExpect(status().isOk())
                .andExpect(view().name("p16-account-type-form"))
                .andExpect(model().attribute("errorMessage", "Account type not found: 999999"));
    }

    @Test
    public void interestRun_shouldRenderReport() throws Exception {
        mockMvc.perform(post("/interest/run")
                        .param("runAtIso", "2026-03-01T00:00:00+00:00"))
                .andExpect(status().isOk())
                .andExpect(view().name("p17-interest"))
                .andExpect(model().attributeExists("report"));
    }

    @Test
    public void interestRun_shouldRenderValidationError_whenTimestampInvalid() throws Exception {
        mockMvc.perform(post("/interest/run")
                        .param("runAtIso", "bad-time"))
                .andExpect(status().isOk())
                .andExpect(view().name("p17-interest"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    public void interestRun_shouldRenderReport_whenTimestampMissing() throws Exception {
        mockMvc.perform(post("/interest/run")
                        .param("runAtIso", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("p17-interest"))
                .andExpect(model().attributeExists("report"));
    }

    @Test
    public void interestRun_shouldSkipClosedAccounts() throws Exception {
        InterestRunResult report = runInterestAndGetReport("2026-03-01T00:00:00+00:00");
        assertTrue(report.getSkippedAccounts().contains("ACC-0007: closed"));
        assertTrue(report.getSkippedAccounts().contains("ACC-0008: closed"));
    }

    @Test
    public void interestRun_shouldSkipAccountsWithZeroInterestRate() throws Exception {
        InterestRunResult report = runInterestAndGetReport("2026-03-01T00:00:00+00:00");
        assertTrue(report.getSkippedAccounts().contains("ACC-0001: zero interest rate"));
        assertTrue(report.getSkippedAccounts().contains("ACC-0006: zero interest rate"));
    }

    @Test
    public void interestRun_shouldSkipAccountsWithNonPositiveBalance() throws Exception {
        Account account = accountDao.findById(2L).orElseThrow();
        account.setBalance(new BigDecimal("0.00"));
        accountDao.save(account);

        InterestRunResult report = runInterestAndGetReport("2026-03-01T00:00:00+00:00");
        assertTrue(report.getSkippedAccounts().contains("ACC-0002: non-positive balance"));
    }

    @Test
    public void interestRun_shouldSkipAccountsWhenCalculatedInterestIsZero() throws Exception {
        AccountType savingsType = accountTypeDao.findById(1L).orElseThrow();
        savingsType.setInterestRate(new BigDecimal("0.0001"));
        accountTypeDao.save(savingsType);

        Account account = accountDao.findById(2L).orElseThrow();
        account.setBalance(new BigDecimal("1.00"));
        accountDao.save(account);

        InterestRunResult report = runInterestAndGetReport("2026-03-01T00:00:00+00:00");
        assertTrue(report.getSkippedAccounts().contains("ACC-0002: calculated interest is zero"));
    }

    @Test
    public void interestRun_shouldProcessInterestToSameAccount() throws Exception {
        InterestRunResult report = runInterestAndGetReport("2026-03-01T00:00:00+00:00");
        assertTrue(report.getProcessedAccounts().contains("ACC-0002 -> ACC-0002"));
    }

    @Test
    public void interestRun_shouldProcessInterestToTargetAccount() throws Exception {
        InterestRunResult report = runInterestAndGetReport("2026-03-01T00:00:00+00:00");
        assertTrue(report.getProcessedAccounts().contains("ACC-0005 -> ACC-0004"));
    }

    @Test
    public void interestRun_shouldBuildCompleteSummaryForRun() throws Exception {
        InterestRunResult report = runInterestAndGetReport("2026-03-01T00:00:00+00:00");
        assertEquals(report.getCreatedOperations(), 2);
        assertEquals(report.getProcessedAccounts().size(), 2);
        assertEquals(report.getTotalInterest(), new BigDecimal("26.24"));
    }

    @Test
    public void requests_shouldWorkUnderNonRootContextPath() throws Exception {
        mockMvc.perform(get("/bankinfo/branches")
                        .contextPath("/bankinfo")
                        .servletPath("/branches"))
                .andExpect(status().isOk())
                .andExpect(view().name("p2-branches"));

        mockMvc.perform(get("/bankinfo/clients")
                        .contextPath("/bankinfo")
                        .servletPath("/clients"))
                .andExpect(status().isOk())
                .andExpect(view().name("p5-clients"));
    }

    private InterestRunResult runInterestAndGetReport(String runAtIso) throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/interest/run").param("runAtIso", runAtIso))
                .andExpect(status().isOk())
                .andExpect(view().name("p17-interest"))
                .andExpect(model().attributeExists("report"))
                .andReturn();
        return (InterestRunResult) mvcResult.getModelAndView().getModel().get("report");
    }
}
