<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Branch</title></head>
<body>
<%@ include file="_nav.jspf" %>
<%@ include file="_alerts.jspf" %>
<h1>Branch Card</h1>
<p>ID: ${branch.id}</p>
<p>Name: ${branch.name}</p>
<p>Address: ${branch.address}</p>
<p><a href="${ctx}/branches/${branch.id}/edit">Edit</a></p>
<form method="post" action="${ctx}/branches/${branch.id}/delete">
    <button type="submit">Delete</button>
</form>
<h2>Accounts</h2>
<table border="1" cellpadding="4" cellspacing="0">
    <tr><th>ID</th><th>Number</th><th>Status</th><th>Balance</th><th>Action</th></tr>
    <c:forEach var="account" items="${branch.accounts}">
        <tr>
            <td>${account.id}</td>
            <td>${account.accountNumber}</td>
            <td>${account.status}</td>
            <td>${account.balance}</td>
            <td><a href="${ctx}/accounts/${account.id}">Open</a></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
