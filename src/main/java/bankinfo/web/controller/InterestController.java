package bankinfo.web.controller;

import bankinfo.web.form.InterestRunForm;
import bankinfo.web.service.AccountService;
import bankinfo.web.service.InterestRunResult;
import bankinfo.web.service.ValidationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

@Controller
@RequestMapping("/interest")
public class InterestController {

    private final AccountService accountService;

    public InterestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public String interestPage(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new InterestRunForm());
        }
        return "p17-interest";
    }

    @PostMapping("/run")
    public String run(@ModelAttribute("form") InterestRunForm form, Model model) {
        try {
            OffsetDateTime runAt = parseRunAt(form.getRunAtIso());
            InterestRunResult report = accountService.runInterest(runAt);
            model.addAttribute("report", report);
        } catch (ValidationException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
        }
        model.addAttribute("form", form);
        return "p17-interest";
    }

    private OffsetDateTime parseRunAt(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(raw.trim());
        } catch (DateTimeParseException exception) {
            throw new ValidationException("Invalid run timestamp format. Expected ISO offset date-time");
        }
    }
}
