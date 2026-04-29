<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Close Account</title></head>
<body>
<%@ include file="_nav.jspf" %>
<%@ include file="_alerts.jspf" %>
<h1>Close Account</h1>
<p>Account: ${account.accountNumber}</p>
<p>Status: ${account.status}</p>
<p>Current balance: ${account.balance}</p>
<form method="post" action="${ctx}/accounts/${account.id}/close">
    <button type="submit">Confirm close</button>
    <a href="${ctx}/accounts/${account.id}">Cancel</a>
</form>
</body>
</html>
