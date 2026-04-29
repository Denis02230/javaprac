package bankinfo.web.form;

import bankinfo.model.TxType;

import java.math.BigDecimal;

public class AccountOperationForm {

    private TxType txType;
    private BigDecimal amount;
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
