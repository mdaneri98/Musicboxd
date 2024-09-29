<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
  <spring:message var="pageTitle" text="Musicboxd"/>
  <jsp:include page="/WEB-INF/jsp/components/head.jsp">
    <jsp:param name="title" value="${pageTitle}"/>
  </jsp:include>

  <c:url var="css" value="/static/css/register.css" />
  <link rel="stylesheet" href="${css}">
</head>
<body>
<div class="container">
  <h1>Musicboxd</h1>
  <c:url var="posturl" value="/user/register" />
  <form:form modelAttribute="userForm" action="${posturl}" method="post">
    <div class="form-group">
      <label> Username <form:input path="username" type="text" /></label>
      <form:errors path="username" element="p" cssStyle="color:red;"/>
    </div>
    <div class="form-group">
      <label> Email <form:input path="email" type="text" /></label>
      <form:errors path="email" element="p" cssStyle="color:red;"/>
    </div>
    <div class="form-group">
      <label> Password <form:input path="password" type="password"/></label>
      <form:errors path="password" element="p" cssStyle="color:red;"/>
    </div>
    <div class="form-group">
      <label> Repeat password <form:input path="repeatPassword" type="password"/></label>
      <form:errors path="repeatPassword" cssStyle="color:error;" element="p"/>
    </div>
    <!-- Mostrar errores a nivel de la clase para la validación de @PasswordMatch -->
    <div class="form-group">
      <form:errors path="" element="p" cssStyle="color:red;" />
    </div>
    <button type="submit" class="button">Register</button>
  </form:form>
  <div class="register-link">
    <c:url var="loginUrl" value="/user/login" />
    <a href="${loginUrl}">Already have an account?</a>
  </div>
</div>

</body>
</html>