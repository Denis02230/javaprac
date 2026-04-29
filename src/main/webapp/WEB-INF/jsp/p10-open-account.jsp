<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Open Account</title></head>
<body>
<%@ include file="_nav.jspf" %>
<%@ include file="_alerts.jspf" %>
<h1>Open Account</h1>
<form method="post" action="${ctx}/accounts/open">
    <p>
        Client:
        <select name="clientId">
            <c:forEach var="client" items="${clients}">
                <option value="${client.id}">${client.displayName}</option>
            </c:forEach>
        </select>
    </p>
    <p>
        Branch:
        <select name="branchId">
            <c:forEach var="branch" items="${branches}">
                <option value="${branch.id}">${branch.name}</option>
            </c:forEach>
        </select>
    </p>
    <p>
        Account Type:
        <select name="accountTypeId">
            <c:forEach var="accountType" items="${accountTypes}">
                <option value="${accountType.id}">${accountType.name}</option>
            </c:forEach>
        </select>
    </p>
    <button type="submit">Create</button>
    <a href="${ctx}/accounts">Cancel</a>
</form>
</body>
</html>
