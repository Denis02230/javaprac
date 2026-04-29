package bankinfo.web.controller;

import bankinfo.model.AccountTx;
import bankinfo.model.TxType;
import bankinfo.web.service.OperationService;
import bankinfo.web.service.ValidationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
@RequestMapping("/operations")
public class OperationController {

    private final OperationService operationService;

    public OperationController(OperationService operationService) {
        this.operationService = operationService;
    }

    @GetMapping
    public String listOperations(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to,
            @RequestParam(value = "type", required = false) TxType txType,
            @RequestParam(value = "accountId", required = false) Long accountId,
            @RequestParam(value = "clientId", required = false) Long clientId,
            @RequestParam(value = "branchId", required = false) Long branchId,
            @RequestParam(value = "accountTypeId", required = false) Long accountTypeId,
            Model model
    ) {
        List<AccountTx> operations;
        try {
            OffsetDateTime fromTime = parseDateTime(from);
            OffsetDateTime toTime = parseDateTime(to);
            if (fromTime != null && toTime != null && fromTime.isAfter(toTime)) {
                throw new ValidationException("Period start must be before or equal to period end");
            }

            operations = operationService.findOperations(
                    fromTime,
                    toTime,
                    txType,
                    accountId,
                    clientId,
                    branchId,
                    accountTypeId
            );
        } catch (ValidationException exception) {
            operations = operationService.findOperations(
                    null,
                    null,
                    txType,
                    accountId,
                    clientId,
                    branchId,
                    accountTypeId
            );
            model.addAttribute("errorMessage", exception.getMessage());
        }

        model.addAttribute("operations", operations);
        model.addAttribute("from", from == null ? "" : from);
        model.addAttribute("to", to == null ? "" : to);
        model.addAttribute("selectedType", txType);
        model.addAttribute("allTypes", TxType.values());
        model.addAttribute("accountId", accountId);
        model.addAttribute("clientId", clientId);
        model.addAttribute("branchId", branchId);
        model.addAttribute("accountTypeId", accountTypeId);

        return "p13-operations";
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
}
