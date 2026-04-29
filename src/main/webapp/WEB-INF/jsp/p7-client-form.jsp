<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Client Form</title></head>
<body>
<%@ include file="_nav.jspf" %>
<%@ include file="_alerts.jspf" %>
<h1>Client Form</h1>
<form method="post" action="${ctx}/clients/save">
    <input type="hidden" name="id" value="${form.id}"/>
    <p>
        Type:
        <select name="clientType">
            <c:forEach var="clientType" items="${allTypes}">
                <option value="${clientType}" <c:if test="${form.clientType == clientType}">selected</c:if>>${clientType}</option>
            </c:forEach>
        </select>
    </p>
    <p>Name: <input type="text" name="displayName" value="${form.displayName}"/></p>
    <button type="submit">Save</button>
    <a href="${ctx}/clients">Cancel</a>
</form>
</body>
</html>
