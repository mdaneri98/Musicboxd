<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
  <title>Create form</title>
</head>
<body>

<c:url var="posturl" value="/user/register" />
<form:form modelAttribute="userForm" action="${posturl}" method="post">
  <div>
    <label> Username <form:input path="username" type="text" /></label>
    <form:errors path="username" element="p" cssStyle="color:red;"/>
  </div>
  <div>
    <label> Email <form:input path="email" type="text" /></label>
    <form:errors path="email" element="p" cssStyle="color:red;"/>
  </div>
  <div>
    <label> Password <form:input path="password" type="password" placeholder="Passowrd"/></label>
    <form:errors path="password" element="p" cssStyle="color:red;"/>
  </div>
  <div>
    <label> Repeat password <form:input path="repeatPassword" type="password" placeholder="Repeat password"/></label>
    <form:errors path="repeatPassword" cssStyle="color:error;" element="p"/>
  </div>
  <div>
    <input type="submit">
  </div>
</form:form>

<c:url var="loginUrl" value="/user/login" />
<a href="${loginUrl}">¿Ya tenes una cuenta?</a>


</body>
</html>
