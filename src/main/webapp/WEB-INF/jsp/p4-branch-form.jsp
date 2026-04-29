<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Branch Form</title></head>
<body>
<%@ include file="_nav.jspf" %>
<%@ include file="_alerts.jspf" %>
<h1>Branch Form</h1>
<form method="post" action="${ctx}/branches/save">
    <input type="hidden" name="id" value="${form.id}"/>
    <p>Name: <input type="text" name="name" value="${form.name}"/></p>
    <p>Address: <input type="text" name="address" value="${form.address}"/></p>
    <button type="submit">Save</button>
    <a href="${ctx}/branches">Cancel</a>
</form>
</body>
</html>
