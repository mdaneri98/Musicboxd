<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <h1><spring:message code="webpage.name"/></h1>

    <c:url var="loginUrl" value="/user/login" />
    <form action="${loginUrl}" method="post">
        <div class="form-group">
            <label for="username"><spring:message code="label.username"/></label>
            <input type="text" id="username" name="username" required>
        </div>
        <div class="form-group">
            <label for="password"><spring:message code="label.password"/></label>
            <input type="password" id="password" name="password" required>
        </div>
        <div class="form-group">
            <label>
                <input type="checkbox" name="remember_me">
                <spring:message code="label.remember.me"/>
            </label>
        </div>
        <button type="submit" class="button"><spring:message code="label.login"/></button>
    </form>
    <div class="register-link">
        <c:url var="registerUrl" value="/user/register" />
        <a href="${registerUrl}"><spring:message code="label.dont.you.have.an.account.yet"/></a>
        <c:url var="changepasswordUrl" value="/user/forgot-password" />
        <a href="${changepasswordUrl}" class="change-password"><spring:message code="label.change.password"/></a>
    </div>
</div>
</body>
</html>
