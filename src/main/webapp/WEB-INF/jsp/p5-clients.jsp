<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Clients</title></head>
<body>
<%@ include file="_nav.jspf" %>
<%@ include file="_alerts.jspf" %>
<h1>Client List</h1>
<form method="get" action="${ctx}/clients">
    <input type="text" name="q" value="${query}" placeholder="Search by name"/>
    <select name="type">
        <option value="">All types</option>
        <c:forEach var="clientType" items="${allTypes}">
            <option value="${clientType}" <c:if test="${selectedType == clientType}">selected</c:if>>${clientType}</option>
        </c:forEach>
    </select>
    <button type="submit">Filter</button>
</form>
<p><a href="${ctx}/clients/form">Add client</a></p>
<table border="1" cellpadding="4" cellspacing="0">
    <tr><th>ID</th><th>Type</th><th>Name</th><th>Action</th></tr>
    <c:forEach var="client" items="${clients}">
        <tr>
            <td>${client.id}</td>
            <td>${client.clientType}</td>
            <td>${client.displayName}</td>
            <td><a href="${ctx}/clients/${client.id}">Open</a></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
