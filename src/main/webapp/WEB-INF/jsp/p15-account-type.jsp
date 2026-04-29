<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Account Type</title></head>
<body>
<%@ include file="_nav.jspf" %>
<%@ include file="_alerts.jspf" %>
<h1>Account Type Card</h1>
<p>ID: ${accountType.id}</p>
<p>Name: ${accountType.name}</p>
<p>Max credit: ${accountType.maxCredit}</p>
<p>Credit repay rule: ${accountType.creditRepayRule}</p>
<p>Interest rate: ${accountType.interestRate}</p>
<p>Interest interval: ${accountType.interestInterval}</p>
<p>Interest method: ${accountType.interestMethod}</p>
<p>Allow debit: ${accountType.allowDebit}</p>
<p>Allow credit: ${accountType.allowCredit}</p>
<p>Credit limits: ${accountType.minCreditAmount} .. ${accountType.maxCreditAmount}</p>
<p>Debit limits: ${accountType.minDebitAmount} .. ${accountType.maxDebitAmount}</p>
<p><a href="${ctx}/account-types/${accountType.id}/edit">Edit</a></p>
</body>
</html>
