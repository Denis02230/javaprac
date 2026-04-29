<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Operations</title></head>
<body>
<%@ include file="_nav.jspf" %>
<%@ include file="_alerts.jspf" %>
<h1>Operations Journal</h1>
<form method="get" action="${ctx}/operations">
    <input type="text" name="from" value="${from}" placeholder="From ISO time"/>
    <input type="text" name="to" value="${to}" placeholder="To ISO time"/>
    <select name="type">
        <option value="">All types</option>
        <c:forEach var="type" items="${allTypes}">
            <option value="${type}" <c:if test="${selectedType == type}">selected</c:if>>${type}</option>
        </c:forEach>
    </select>
    <input type="text" name="accountId" value="${accountId}" placeholder="Account ID"/>
    <input type="text" name="clientId" value="${clientId}" placeholder="Client ID"/>
    <input type="text" name="branchId" value="${branchId}" placeholder="Branch ID"/>
    <input type="text" name="accountTypeId" value="${accountTypeId}" placeholder="Account Type ID"/>
    <button type="submit">Filter</button>
</form>
<table border="1" cellpadding="4" cellspacing="0">
    <tr><th>ID</th><th>Time</th><th>Type</th><th>Amount</th><th>Account</th><th>Client</th><th>Branch</th><th>Action</th></tr>
    <c:forEach var="op" items="${operations}">
        <tr>
            <td>${op.id}</td>
            <td>${op.txTime}</td>
            <td>${op.txType}</td>
            <td>${op.amount}</td>
            <td>${op.account.accountNumber}</td>
            <td>${op.account.client.displayName}</td>
            <td>${op.account.branch.name}</td>
            <td><a href="${ctx}/accounts/${op.account.id}">Open account</a></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
