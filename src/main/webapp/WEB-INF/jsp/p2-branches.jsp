<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Branches</title></head>
<body>
<%@ include file="_nav.jspf" %>
<%@ include file="_alerts.jspf" %>
<h1>Branch List</h1>
<form method="get" action="${ctx}/branches">
    <input type="text" name="q" value="${query}" placeholder="Search by name"/>
    <button type="submit">Search</button>
</form>
<p><a href="${ctx}/branches/form">Add branch</a></p>
<table border="1" cellpadding="4" cellspacing="0">
    <tr><th>ID</th><th>Name</th><th>Address</th><th>Action</th></tr>
    <c:forEach var="branch" items="${branches}">
        <tr>
            <td>${branch.id}</td>
            <td>${branch.name}</td>
            <td>${branch.address}</td>
            <td><a href="${ctx}/branches/${branch.id}">Open</a></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
