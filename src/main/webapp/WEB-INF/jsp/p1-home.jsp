<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Home</title></head>
<body>
<%@ include file="_nav.jspf" %>
<%@ include file="_alerts.jspf" %>
<h1>Main Navigation</h1>
<p>Bank reference system web interface.</p>
<ul>
    <li><a href="${ctx}/branches">Branches</a></li>
    <li><a href="${ctx}/clients">Clients</a></li>
    <li><a href="${ctx}/accounts">Accounts</a></li>
    <li><a href="${ctx}/operations">Operations</a></li>
    <li><a href="${ctx}/account-types">Account Types</a></li>
    <li><a href="${ctx}/interest">Interest Run</a></li>
</ul>
</body>
</html>
