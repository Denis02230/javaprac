package bankinfo.web.controller;

import bankinfo.model.AccountType;
import bankinfo.model.InterestInterval;
import bankinfo.model.InterestMethod;
import bankinfo.web.form.AccountTypeForm;
import bankinfo.web.service.AccountTypeService;
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

import java.util.List;

@Controller
@RequestMapping("/account-types")
public class AccountTypeController {

    private final AccountTypeService accountTypeService;

    public AccountTypeController(AccountTypeService accountTypeService) {
        this.accountTypeService = accountTypeService;
    }

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String query, Model model) {
        List<AccountType> accountTypes = accountTypeService.findAccountTypes(query);
        model.addAttribute("accountTypes", accountTypes);
        model.addAttribute("query", query == null ? "" : query);
        return "p14-account-types";
    }

    @GetMapping("/{id}")
    public String card(@PathVariable("id") Long id, Model model) {
        AccountType accountType = accountTypeService.findById(id)
                .orElseThrow(() -> new ValidationException("Account type not found: " + id));
        model.addAttribute("accountType", accountType);
        return "p15-account-type";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        AccountType accountType = accountTypeService.findById(id)
                .orElseThrow(() -> new ValidationException("Account type not found: " + id));

        AccountTypeForm form = new AccountTypeForm();
        form.setId(accountType.getId());
        form.setName(accountType.getName());
        form.setMaxCredit(accountType.getMaxCredit());
        form.setCreditRepayRule(accountType.getCreditRepayRule());
        form.setInterestRate(accountType.getInterestRate());
        form.setInterestInterval(accountType.getInterestInterval());
        form.setInterestMethod(accountType.getInterestMethod());
        form.setAllowDebit(accountType.getAllowDebit());
        form.setAllowCredit(accountType.getAllowCredit());
        form.setMinCreditAmount(accountType.getMinCreditAmount());
        form.setMaxCreditAmount(accountType.getMaxCreditAmount());
        form.setMinDebitAmount(accountType.getMinDebitAmount());
        form.setMaxDebitAmount(accountType.getMaxDebitAmount());

        model.addAttribute("form", form);
        model.addAttribute("allIntervals", InterestInterval.values());
        model.addAttribute("allMethods", InterestMethod.values());
        return "p16-account-type-form";
    }

    @PostMapping("/{id}/save")
    public String save(
            @PathVariable("id") Long id,
            @ModelAttribute("form") AccountTypeForm form,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            AccountType accountType = accountTypeService.findById(id)
                    .orElseThrow(() -> new ValidationException("Account type not found: " + id));

            accountType.setName(form.getName());
            accountType.setMaxCredit(form.getMaxCredit());
            accountType.setCreditRepayRule(form.getCreditRepayRule());
            accountType.setInterestRate(form.getInterestRate());
            accountType.setInterestInterval(form.getInterestInterval());
            accountType.setInterestMethod(form.getInterestMethod());
            accountType.setAllowDebit(form.getAllowDebit());
            accountType.setAllowCredit(form.getAllowCredit());
            accountType.setMinCreditAmount(form.getMinCreditAmount());
            accountType.setMaxCreditAmount(form.getMaxCreditAmount());
            accountType.setMinDebitAmount(form.getMinDebitAmount());
            accountType.setMaxDebitAmount(form.getMaxDebitAmount());

            accountTypeService.save(accountType);

            redirectAttributes.addFlashAttribute("message", "Account type saved");
            return "redirect:/account-types/" + id;
        } catch (ValidationException exception) {
            model.addAttribute("form", form);
            model.addAttribute("allIntervals", InterestInterval.values());
            model.addAttribute("allMethods", InterestMethod.values());
            model.addAttribute("errorMessage", exception.getMessage());
            return "p16-account-type-form";
        }
    }
}
