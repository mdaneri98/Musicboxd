<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <spring:message var="pageTitle" code="webpage.name"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${pageTitle}"/>
    </jsp:include>

    <c:url var="css" value="/static/css/register.css" />
    <link rel="stylesheet" href="${css}">
</head>
<body>
<div class="container">
    <h1><p><spring:message code="webpage.name"/></p></h1>
    <c:url var="posturl" value="/user/forgot-password" />
    <form action="${posturl}" method="post">
        <div class="form-group">
            <label> <spring:message code="label.email"/>
                <input type="email" name="email" required />
            </label>
        </div>
        <button type="submit" class="button">
            <spring:message code="button.change.password.send.email"/>
        </button>
    </form>
    <div class="register-link">
        <c:url var="loginUrl" value="/user/login" />
        <a href="${loginUrl}"><spring:message code="label.already.have.an.account"/></a>
    </div>
</div>

</body>
</html>