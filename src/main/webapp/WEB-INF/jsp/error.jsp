<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Error</title></head>
<body>
<%@ include file="_nav.jspf" %>
<%@ include file="_alerts.jspf" %>
<h1>${errorTitle}</h1>
<p>${errorMessage}</p>
<p><a href="${ctx}/">Back to home</a></p>
</body>
</html>
