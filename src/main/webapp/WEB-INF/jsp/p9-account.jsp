<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Account</title></head>
<body>
<%@ include file="_nav.jspf" %>
<%@ include file="_alerts.jspf" %>
<h1>Account Card</h1>
<p>ID: ${account.id}</p>
<p>Number: ${account.accountNumber}</p>
<p>Status: ${account.status}</p>
<p>Balance: ${account.balance}</p>
<p>Client: <a href="${ctx}/clients/${account.client.id}">${account.client.displayName}</a></p>
<p>Branch: <a href="${ctx}/branches/${account.branch.id}">${account.branch.name}</a></p>
<p>Account Type: <a href="${ctx}/account-types/${account.accountType.id}">${account.accountType.name}</a></p>
<p>
    <a href="${ctx}/accounts/${account.id}/tx?type=CREDIT">Credit</a> |
    <a href="${ctx}/accounts/${account.id}/tx?type=DEBIT">Debit</a> |
    <a href="${ctx}/accounts/${account.id}/close">Close account</a>
</p>
<h2>Operations Period Filter</h2>
<form method="get" action="${ctx}/accounts/${account.id}">
    <input type="text" name="from" value="${from}" placeholder="2026-02-01T00:00:00+00:00"/>
    <input type="text" name="to" value="${to}" placeholder="2026-02-28T23:59:59+00:00"/>
    <button type="submit">Apply</button>
</form>
<h2>Transactions</h2>
<table border="1" cellpadding="4" cellspacing="0">
    <tr><th>ID</th><th>Time</th><th>Type</th><th>Amount</th><th>Comment</th></tr>
    <c:forEach var="tx" items="${transactions}">
        <tr>
            <td>${tx.id}</td>
            <td>${tx.txTime}</td>
            <td>${tx.txType}</td>
            <td>${tx.amount}</td>
            <td>${tx.description}</td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
