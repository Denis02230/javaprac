<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Client</title></head>
<body>
<%@ include file="_nav.jspf" %>
<%@ include file="_alerts.jspf" %>
<h1>Client Card</h1>
<p>ID: ${client.id}</p>
<p>Type: ${client.clientType}</p>
<p>Name: ${client.displayName}</p>
<p>Created: ${client.createdAt}</p>
<p>
    <a href="${ctx}/clients/${client.id}/edit">Edit</a> |
    <a href="${ctx}/accounts/open">Open account</a>
</p>
<form method="post" action="${ctx}/clients/${client.id}/delete">
    <button type="submit">Delete</button>
</form>

<h2>Addresses</h2>
<ul>
    <c:forEach var="address" items="${client.addresses}">
        <li>${address.addressType}: ${address.address}</li>
    </c:forEach>
</ul>

<h2>Phones</h2>
<ul>
    <c:forEach var="phone" items="${client.phones}">
        <li>${phone.phoneType}: ${phone.phone}</li>
    </c:forEach>
</ul>

<h2>Emails</h2>
<ul>
    <c:forEach var="email" items="${client.emails}">
        <li>${email.emailType}: ${email.email}</li>
    </c:forEach>
</ul>

<h2>Contact Persons</h2>
<ul>
    <c:forEach var="contact" items="${client.contactPersons}">
        <li>${contact.fullName} (${contact.position}) ${contact.phone} ${contact.email}</li>
    </c:forEach>
</ul>

<h2>Accounts</h2>
<table border="1" cellpadding="4" cellspacing="0">
    <tr><th>ID</th><th>Number</th><th>Status</th><th>Balance</th><th>Action</th></tr>
    <c:forEach var="account" items="${accounts}">
        <tr>
            <td>${account.id}</td>
            <td>${account.accountNumber}</td>
            <td>${account.status}</td>
            <td>${account.balance}</td>
            <td><a href="${ctx}/accounts/${account.id}">Open</a></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
