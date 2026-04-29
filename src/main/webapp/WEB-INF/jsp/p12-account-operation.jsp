<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Account Operation</title></head>
<body>
<%@ include file="_nav.jspf" %>
<%@ include file="_alerts.jspf" %>
<h1>Account Operation</h1>
<p>Account: ${account.accountNumber}</p>
<p>Balance: ${account.balance}</p>
<form method="post" action="${ctx}/accounts/${account.id}/tx">
    <p>
        Type:
        <select name="txType">
            <c:forEach var="txType" items="${allTypes}">
                <option value="${txType}" <c:if test="${form.txType == txType}">selected</c:if>>${txType}</option>
            </c:forEach>
        </select>
    </p>
    <p>Amount: <input type="text" name="amount" value="${form.amount}"/></p>
    <p>Comment: <input type="text" name="description" value="${form.description}"/></p>
    <button type="submit">Post operation</button>
    <a href="${ctx}/accounts/${account.id}">Cancel</a>
</form>
</body>
</html>
