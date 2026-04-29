package bankinfo.web.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InterestRunResult {

    private int createdOperations;
    private final List<String> processedAccounts = new ArrayList<>();
    private final List<String> skippedAccounts = new ArrayList<>();
    private BigDecimal totalInterest = BigDecimal.ZERO;

    public int getCreatedOperations() {
        return createdOperations;
    }

    public void incrementCreatedOperations() {
        this.createdOperations++;
    }

    public List<String> getProcessedAccounts() {
        return Collections.unmodifiableList(processedAccounts);
    }

    public void addProcessedAccount(String accountNumber) {
        processedAccounts.add(accountNumber);
    }

    public List<String> getSkippedAccounts() {
        return Collections.unmodifiableList(skippedAccounts);
    }

    public void addSkippedAccount(String reason) {
        skippedAccounts.add(reason);
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }

    public void addTotalInterest(BigDecimal amount) {
        totalInterest = totalInterest.add(amount);
    }
}
