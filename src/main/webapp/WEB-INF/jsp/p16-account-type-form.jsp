<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Account Type Form</title></head>
<body>
<%@ include file="_nav.jspf" %>
<%@ include file="_alerts.jspf" %>
<h1>Account Type Form</h1>
<form method="post" action="${ctx}/account-types/${form.id}/save">
    <p>Name: <input type="text" name="name" value="${form.name}"/></p>
    <p>Max credit: <input type="text" name="maxCredit" value="${form.maxCredit}"/></p>
    <p>Credit repay rule: <input type="text" name="creditRepayRule" value="${form.creditRepayRule}"/></p>
    <p>Interest rate: <input type="text" name="interestRate" value="${form.interestRate}"/></p>

    <p>
        Interest interval:
        <select name="interestInterval">
            <c:forEach var="interval" items="${allIntervals}">
                <option value="${interval}" <c:if test="${form.interestInterval == interval}">selected</c:if>>${interval}</option>
            </c:forEach>
        </select>
    </p>

    <p>
        Interest method:
        <select name="interestMethod">
            <c:forEach var="method" items="${allMethods}">
                <option value="${method}" <c:if test="${form.interestMethod == method}">selected</c:if>>${method}</option>
            </c:forEach>
        </select>
    </p>

    <p>
        Allow debit:
        <select name="allowDebit">
            <option value="true" <c:if test="${form.allowDebit}">selected</c:if>>true</option>
            <option value="false" <c:if test="${!form.allowDebit}">selected</c:if>>false</option>
        </select>
    </p>
    <p>
        Allow credit:
        <select name="allowCredit">
            <option value="true" <c:if test="${form.allowCredit}">selected</c:if>>true</option>
            <option value="false" <c:if test="${!form.allowCredit}">selected</c:if>>false</option>
        </select>
    </p>

    <p>Min credit amount: <input type="text" name="minCreditAmount" value="${form.minCreditAmount}"/></p>
    <p>Max credit amount: <input type="text" name="maxCreditAmount" value="${form.maxCreditAmount}"/></p>
    <p>Min debit amount: <input type="text" name="minDebitAmount" value="${form.minDebitAmount}"/></p>
    <p>Max debit amount: <input type="text" name="maxDebitAmount" value="${form.maxDebitAmount}"/></p>

    <button type="submit">Save</button>
    <a href="${ctx}/account-types/${form.id}">Cancel</a>
</form>
</body>
</html>
