package bankinfo.web.service;

import bankinfo.dao.AccountTxDao;
import bankinfo.model.AccountTx;
import bankinfo.model.TxType;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OperationService {

    private final AccountTxDao accountTxDao;

    public OperationService(AccountTxDao accountTxDao) {
        this.accountTxDao = accountTxDao;
    }

    public List<AccountTx> findOperations(
            OffsetDateTime from,
            OffsetDateTime to,
            TxType txType,
            Long accountId,
            Long clientId,
            Long branchId,
            Long accountTypeId
    ) {
        List<AccountTx> operations;

        if (from != null && to != null) {
            operations = accountTxDao.findByPeriodDetailed(from, to);
        } else {
            operations = accountTxDao.findAllDetailed();
        }

        List<AccountTx> filtered = new ArrayList<>();
        for (AccountTx tx : operations) {
            if (txType != null && tx.getTxType() != txType) {
                continue;
            }
            if (accountId != null && !tx.getAccount().getId().equals(accountId)) {
                continue;
            }
            if (clientId != null && !tx.getAccount().getClient().getId().equals(clientId)) {
                continue;
            }
            if (branchId != null && !tx.getAccount().getBranch().getId().equals(branchId)) {
                continue;
            }
            if (accountTypeId != null && !tx.getAccount().getAccountType().getId().equals(accountTypeId)) {
                continue;
            }
            filtered.add(tx);
        }

        return filtered;
    }
}
