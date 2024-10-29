<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <spring:message var="pageTitle" code="webpage.name"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${pageTitle}"/>
    </jsp:include>
</head>
<body>
    <div class="auth-container">
        <div class="auth-card">
            <h1 class="auth-title"><spring:message code="webpage.name"/></h1>

            <c:url var="loginUrl" value="/user/login" />
            <form action="${loginUrl}" method="post" class="auth-form">
                <div class="form-group">
                    <label for="username" class="form-label">
                        <spring:message code="label.username"/>
                    </label>
                    <input type="text" id="username" name="username" class="form-control" required>
                </div>

                <div class="form-group">
                    <label for="password" class="form-label">
                        <spring:message code="label.password"/>
                    </label>
                    <input type="password" id="password" name="password" class="form-control" required>
                </div>

                <div class="form-group">
                    <label class="checkbox-label">
                        <input type="checkbox" name="remember_me">
                        <span class="checkbox-text">
                            <spring:message code="label.remember.me"/>
                        </span>
                    </label>
                </div>

                <button type="submit" class="btn btn-primary btn-block">
                    <spring:message code="label.login"/>
                </button>
            </form>

            <div class="auth-links">
                <c:url var="registerUrl" value="/user/register" />
                <a href="${registerUrl}" class="auth-link">
                    <spring:message code="label.dont.you.have.an.account.yet"/>
                </a>
                <c:url var="changepasswordUrl" value="/user/forgot-password" />
                <a href="${changepasswordUrl}" class="auth-link">
                    <spring:message code="label.change.password"/>
                </a>
            </div>
        </div>
    </div>
</body>
</html>
