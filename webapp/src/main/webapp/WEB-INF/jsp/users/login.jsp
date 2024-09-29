<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <c:url var="loginUrl" value="/user/login" />
    <form action="${loginUrl}" method="post">
        <div class="form-group">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" required>
        </div>
        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" required>
        </div>
        <div class="form-group">
            <label>
                <input type="checkbox" name="remember_me">
                Remember me
            </label>
        </div>
        <button type="submit" class="button">Login</button>
    </form>
    <div class="register-link">
        <c:url var="registerUrl" value="/user/register" />
        <a href="${registerUrl}">Don't have an account yet? Register here</a>
    </div>
</div>
</body>
</html>
