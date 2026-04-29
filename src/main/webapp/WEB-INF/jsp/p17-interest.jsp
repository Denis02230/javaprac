<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Interest Run</title></head>
<body>
<%@ include file="_nav.jspf" %>
<%@ include file="_alerts.jspf" %>
<h1>Interest Run</h1>
<form method="post" action="${ctx}/interest/run">
    <p>Run timestamp (ISO offset datetime, optional):
        <input type="text" name="runAtIso" value="${form.runAtIso}" placeholder="2026-03-01T00:00:00+00:00"/>
    </p>
    <button type="submit">Run interest</button>
</form>

<c:if test="${not empty report}">
    <h2>Run report</h2>
    <p>Created operations: ${report.createdOperations}</p>
    <p>Total interest: ${report.totalInterest}</p>
    <h3>Processed accounts</h3>
    <ul>
        <c:forEach var="line" items="${report.processedAccounts}">
            <li>${line}</li>
        </c:forEach>
    </ul>
    <h3>Skipped accounts</h3>
    <ul>
        <c:forEach var="line" items="${report.skippedAccounts}">
            <li>${line}</li>
        </c:forEach>
    </ul>
    <p><a href="${ctx}/operations">Open operations journal</a></p>
</c:if>
</body>
</html>
