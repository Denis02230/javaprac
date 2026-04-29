<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Accounts</title></head>
<body>
<%@ include file="_nav.jspf" %>
<%@ include file="_alerts.jspf" %>
<h1>Account List</h1>
<form method="get" action="${ctx}/accounts">
    <input type="text" name="q" value="${query}" placeholder="Account number"/>
    <select name="status">
        <option value="">All statuses</option>
        <c:forEach var="status" items="${allStatuses}">
            <option value="${status}" <c:if test="${selectedStatus == status}">selected</c:if>>${status}</option>
        </c:forEach>
    </select>
    <select name="clientId">
        <option value="">All clients</option>
        <c:forEach var="client" items="${clients}">
            <option value="${client.id}" <c:if test="${selectedClientId == client.id}">selected</c:if>>${client.displayName}</option>
        </c:forEach>
    </select>
    <select name="branchId">
        <option value="">All branches</option>
        <c:forEach var="branch" items="${branches}">
            <option value="${branch.id}" <c:if test="${selectedBranchId == branch.id}">selected</c:if>>${branch.name}</option>
        </c:forEach>
    </select>
    <select name="accountTypeId">
        <option value="">All account types</option>
        <c:forEach var="accountType" items="${accountTypes}">
            <option value="${accountType.id}" <c:if test="${selectedAccountTypeId == accountType.id}">selected</c:if>>${accountType.name}</option>
        </c:forEach>
    </select>
    <button type="submit">Filter</button>
</form>
<p><a href="${ctx}/accounts/open">Open account</a></p>
<table border="1" cellpadding="4" cellspacing="0">
    <tr>
        <th>ID</th><th>Number</th><th>Client</th><th>Branch</th><th>Type</th><th>Status</th><th>Balance</th><th>Action</th>
    </tr>
    <c:forEach var="account" items="${accounts}">
        <tr>
            <td>${account.id}</td>
            <td>${account.accountNumber}</td>
            <td>${account.client.displayName}</td>
            <td>${account.branch.name}</td>
            <td>${account.accountType.name}</td>
            <td>${account.status}</td>
            <td>${account.balance}</td>
            <td><a href="${ctx}/accounts/${account.id}">Open</a></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
