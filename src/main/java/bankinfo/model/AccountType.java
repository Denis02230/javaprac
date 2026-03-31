package bankinfo.model;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "account_type")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class AccountType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_type_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "max_credit", nullable = false, precision = 14, scale = 2)
    private BigDecimal maxCredit;

    @Column(name = "credit_repay_rule", nullable = false, length = 400)
    private String creditRepayRule;

    @Column(name = "interest_rate", nullable = false, precision = 7, scale = 4)
    private BigDecimal interestRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "interest_interval", nullable = false, columnDefinition = "interest_interval_enum")
    @Type(type = "pgsql_enum")
    private InterestInterval interestInterval;

    @Enumerated(EnumType.STRING)
    @Column(name = "interest_method", nullable = false, columnDefinition = "interest_method_enum")
    @Type(type = "pgsql_enum")
    private InterestMethod interestMethod;

    @Column(name = "allow_debit", nullable = false)
    private Boolean allowDebit;

    @Column(name = "allow_credit", nullable = false)
    private Boolean allowCredit;

    @Column(name = "min_credit_amount", precision = 14, scale = 2)
    private BigDecimal minCreditAmount;

    @Column(name = "max_credit_amount", precision = 14, scale = 2)
    private BigDecimal maxCreditAmount;

    @Column(name = "min_debit_amount", precision = 14, scale = 2)
    private BigDecimal minDebitAmount;

    @Column(name = "max_debit_amount", precision = 14, scale = 2)
    private BigDecimal maxDebitAmount;

    public AccountType() {
    }

    public Long getId() {
        return id;
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

    @Override
    public String toString() {
        return "AccountType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", maxCredit=" + maxCredit +
                ", interestRate=" + interestRate +
                ", interestInterval=" + interestInterval +
                ", interestMethod=" + interestMethod +
                ", allowDebit=" + allowDebit +
                ", allowCredit=" + allowCredit +
                '}';
    }
}