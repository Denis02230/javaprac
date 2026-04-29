package bankinfo.web.form;

import bankinfo.model.InterestInterval;
import bankinfo.model.InterestMethod;

import java.math.BigDecimal;

public class AccountTypeForm {

    private Long id;
    private String name;
    private BigDecimal maxCredit;
    private String creditRepayRule;
    private BigDecimal interestRate;
    private InterestInterval interestInterval;
    private InterestMethod interestMethod;
    private Boolean allowDebit;
    private Boolean allowCredit;
    private BigDecimal minCreditAmount;
    private BigDecimal maxCreditAmount;
    private BigDecimal minDebitAmount;
    private BigDecimal maxDebitAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMaxCredit() {
        return maxCredit;
    }

    public void setMaxCredit(BigDecimal maxCredit) {
        this.maxCredit = maxCredit;
    }

    public String getCreditRepayRule() {
        return creditRepayRule;
    }

    public void setCreditRepayRule(String creditRepayRule) {
        this.creditRepayRule = creditRepayRule;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public InterestInterval getInterestInterval() {
        return interestInterval;
    }

    public void setInterestInterval(InterestInterval interestInterval) {
        this.interestInterval = interestInterval;
    }

    public InterestMethod getInterestMethod() {
        return interestMethod;
    }

    public void setInterestMethod(InterestMethod interestMethod) {
        this.interestMethod = interestMethod;
    }

    public Boolean getAllowDebit() {
        return allowDebit;
    }

    public void setAllowDebit(Boolean allowDebit) {
        this.allowDebit = allowDebit;
    }

    public Boolean getAllowCredit() {
        return allowCredit;
    }

    public void setAllowCredit(Boolean allowCredit) {
        this.allowCredit = allowCredit;
    }

    public BigDecimal getMinCreditAmount() {
        return minCreditAmount;
    }

    public void setMinCreditAmount(BigDecimal minCreditAmount) {
        this.minCreditAmount = minCreditAmount;
    }

    public BigDecimal getMaxCreditAmount() {
        return maxCreditAmount;
    }

    public void setMaxCreditAmount(BigDecimal maxCreditAmount) {
        this.maxCreditAmount = maxCreditAmount;
    }

    public BigDecimal getMinDebitAmount() {
        return minDebitAmount;
    }

    public void setMinDebitAmount(BigDecimal minDebitAmount) {
        this.minDebitAmount = minDebitAmount;
    }

    public BigDecimal getMaxDebitAmount() {
        return maxDebitAmount;
    }

    public void setMaxDebitAmount(BigDecimal maxDebitAmount) {
        this.maxDebitAmount = maxDebitAmount;
    }
}
