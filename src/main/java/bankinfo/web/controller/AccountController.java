package bankinfo.web.controller;

import bankinfo.model.Account;
import bankinfo.model.AccountStatus;
import bankinfo.model.AccountTx;
import bankinfo.model.TxType;
import bankinfo.web.form.AccountOperationForm;
import bankinfo.web.form.OpenAccountForm;
import bankinfo.web.service.AccountService;
import bankinfo.web.service.AccountTypeService;
import bankinfo.web.service.BranchService;
import bankinfo.web.service.ClientService;
import bankinfo.web.service.ValidationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final ClientService clientService;
    private final BranchService branchService;
    private final AccountTypeService accountTypeService;

    public AccountController(
            AccountService accountService,
            ClientService clientService,
            BranchService branchService,
            AccountTypeService accountTypeService
    ) {
        this.accountService = accountService;
        this.clientService = clientService;
        this.branchService = branchService;
        this.accountTypeService = accountTypeService;
    }

    @GetMapping
    public String listAccounts(
            @RequestParam(value = "status", required = false) AccountStatus status,
            @RequestParam(value = "clientId", required = false) Long clientId,
            @RequestParam(value = "branchId", required = false) Long branchId,
            @RequestParam(value = "accountTypeId", required = false) Long accountTypeId,
            @RequestParam(value = "q", required = false) String accountNumber,
            Model model
    ) {
        List<Account> accounts = accountService.findAccounts(status, clientId, branchId, accountTypeId, accountNumber);

        model.addAttribute("accounts", accounts);
        model.addAttribute("allStatuses", AccountStatus.values());
        model.addAttribute("clients", clientService.findClients(null, null));
        model.addAttribute("branches", branchService.findBranches(null));
        model.addAttribute("accountTypes", accountTypeService.findAccountTypes(null));
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedClientId", clientId);
        model.addAttribute("selectedBranchId", branchId);
        model.addAttribute("selectedAccountTypeId", accountTypeId);
        model.addAttribute("query", accountNumber == null ? "" : accountNumber);

        return "p8-accounts";
    }

    @GetMapping("/{id}")
    public String accountCard(
            @PathVariable("id") Long accountId,
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to,
            Model model
    ) {
        Account account = accountService.findAccountDetailed(accountId)
                .orElseThrow(() -> new ValidationException("Account not found: " + accountId));

        List<AccountTx> transactions;
        try {
            OffsetDateTime fromTime = parseDateTime(from);
            OffsetDateTime toTime = parseDateTime(to);
            if (fromTime != null && toTime != null && fromTime.isAfter(toTime)) {
                throw new ValidationException("Period start must be before or equal to period end");
            }
            transactions = accountService.findTransactions(accountId, fromTime, toTime);
        } catch (ValidationException exception) {
            transactions = accountService.findTransactions(accountId, null, null);
            model.addAttribute("errorMessage", exception.getMessage());
        }

        model.addAttribute("account", account);
        model.addAttribute("transactions", transactions);
        model.addAttribute("from", from == null ? "" : from);
        model.addAttribute("to", to == null ? "" : to);

        return "p9-account";
    }

    @GetMapping("/open")
    public String openAccountForm(Model model) {
        prepareOpenAccountViewModel(model);
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new OpenAccountForm());
        }
        return "p10-open-account";
    }

    @PostMapping("/open")
    public String openAccount(
            @ModelAttribute("form") OpenAccountForm form,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            Account account = accountService.openAccount(form.getClientId(), form.getBranchId(), form.getAccountTypeId());
            redirectAttributes.addFlashAttribute("message", "Account opened");
            return "redirect:/accounts/" + account.getId();
        } catch (ValidationException exception) {
            model.addAttribute("form", form);
            model.addAttribute("errorMessage", exception.getMessage());
            prepareOpenAccountViewModel(model);
            return "p10-open-account";
        }
    }

    @GetMapping("/{id}/close")
    public String closeAccountForm(@PathVariable("id") Long accountId, Model model) {
        Account account = accountService.findAccountDetailed(accountId)
                .orElseThrow(() -> new ValidationException("Account not found: " + accountId));
        model.addAttribute("account", account);
        return "p11-close-account";
    }

    @PostMapping("/{id}/close")
    public String closeAccount(@PathVariable("id") Long accountId, RedirectAttributes redirectAttributes, Model model) {
        try {
            Account account = accountService.closeAccount(accountId);
            redirectAttributes.addFlashAttribute("message", "Account closed");
            return "redirect:/accounts/" + account.getId();
        } catch (ValidationException exception) {
            Account account = accountService.findAccountDetailed(accountId)
                    .orElseThrow(() -> exception);
            model.addAttribute("account", account);
            model.addAttribute("errorMessage", exception.getMessage());
            return "p11-close-account";
        }
    }

    @GetMapping("/{id}/tx")
    public String accountTxForm(
            @PathVariable("id") Long accountId,
            @RequestParam(value = "type", required = false) TxType txType,
            Model model
    ) {
        Account account = accountService.findAccountDetailed(accountId)
                .orElseThrow(() -> new ValidationException("Account not found: " + accountId));

        AccountOperationForm form = new AccountOperationForm();
        form.setTxType(txType == null ? TxType.CREDIT : txType);
        model.addAttribute("account", account);
        model.addAttribute("form", form);
        model.addAttribute("allTypes", new TxType[]{TxType.CREDIT, TxType.DEBIT});
        return "p12-account-operation";
    }

    @PostMapping("/{id}/tx")
    public String createTx(
            @PathVariable("id") Long accountId,
            @ModelAttribute("form") AccountOperationForm form,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            accountService.postTransaction(accountId, form.getTxType(), form.getAmount(), form.getDescription());
            redirectAttributes.addFlashAttribute("message", "Transaction posted");
            return "redirect:/accounts/" + accountId;
        } catch (ValidationException exception) {
            Account account = accountService.findAccountDetailed(accountId)
                    .orElseThrow(() -> exception);
            model.addAttribute("account", account);
            model.addAttribute("form", form);
            model.addAttribute("allTypes", new TxType[]{TxType.CREDIT, TxType.DEBIT});
            model.addAttribute("errorMessage", exception.getMessage());
            return "p12-account-operation";
        }
    }

    private OffsetDateTime parseDateTime(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(raw.trim());
        } catch (DateTimeParseException exception) {
            throw new ValidationException("Invalid date-time format. Expected ISO offset date-time");
        }
    }

    private void prepareOpenAccountViewModel(Model model) {
        model.addAttribute("clients", clientService.findClients(null, null));
        model.addAttribute("branches", branchService.findBranches(null));
        model.addAttribute("accountTypes", accountTypeService.findAccountTypes(null));
    }
}
