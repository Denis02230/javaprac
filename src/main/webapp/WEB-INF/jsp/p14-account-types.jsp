<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Account Types</title></head>
<body>
<%@ include file="_nav.jspf" %>
<%@ include file="_alerts.jspf" %>
<h1>Account Types</h1>
<form method="get" action="${ctx}/account-types">
    <input type="text" name="q" value="${query}" placeholder="Search by name"/>
    <button type="submit">Search</button>
</form>
<table border="1" cellpadding="4" cellspacing="0">
    <tr><th>ID</th><th>Name</th><th>Max credit</th><th>Interest rate</th><th>Action</th></tr>
    <c:forEach var="accountType" items="${accountTypes}">
        <tr>
            <td>${accountType.id}</td>
            <td>${accountType.name}</td>
            <td>${accountType.maxCredit}</td>
            <td>${accountType.interestRate}</td>
            <td><a href="${ctx}/account-types/${accountType.id}">Open</a></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
