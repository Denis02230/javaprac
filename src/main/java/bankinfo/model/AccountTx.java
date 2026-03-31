package bankinfo.model;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "account_tx")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class AccountTx {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tx_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "tx_type", nullable = false, columnDefinition = "tx_type_enum")
    @Type(type = "pgsql_enum")
    private TxType txType;

    @Column(name = "amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(name = "tx_time", nullable = false)
    private OffsetDateTime txTime;

    @Column(name = "comment", length = 400)
    private String description;

    public AccountTx() {
    }

    public Long getId() {
        return id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public TxType getTxType() {
        return txType;
    }

    public void setTxType(TxType txType) {
        this.txType = txType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public OffsetDateTime getTxTime() {
        return txTime;
    }

    public void setTxTime(OffsetDateTime txTime) {
        this.txTime = txTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "AccountTx{" +
                "id=" + id +
                ", txType=" + txType +
                ", amount=" + amount +
                ", txTime=" + txTime +
                ", description='" + description + '\'' +
                '}';
    }
}