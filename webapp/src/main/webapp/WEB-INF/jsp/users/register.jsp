<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
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

            <c:url var="posturl" value="/user/register" />
            <form:form modelAttribute="userForm" action="${posturl}" method="post" class="auth-form">
                <div class="form-group">
                    <label class="form-label">
                        <spring:message code="label.username"/>
                    </label>
                    <form:input path="username" type="text" cssClass="form-control"/>
                    <form:errors path="username" cssClass="form-error"/>
                </div>

                <div class="form-group">
                    <label class="form-label">
                        <spring:message code="label.email"/>
                    </label>
                    <form:input path="email" type="text" cssClass="form-control"/>
                    <form:errors path="email" cssClass="form-error"/>
                </div>

                <div class="form-group">
                    <label class="form-label">
                        <spring:message code="label.password"/>
                    </label>
                    <form:input path="password" type="password" cssClass="form-control"/>
                    <form:errors path="password" cssClass="form-error"/>
                </div>

                <div class="form-group">
                    <label class="form-label">
                        <spring:message code="label.repeat.password"/>
                    </label>
                    <form:input path="repeatPassword" type="password" cssClass="form-control"/>
                    <form:errors path="repeatPassword" cssClass="form-error"/>
                </div>

                <form:errors path="" cssClass="form-error"/>

                <button type="submit" class="btn btn-primary btn-block">
                    <spring:message code="label.register"/>
                </button>
            </form:form>

            <div class="auth-links">
                <c:url var="loginUrl" value="/user/login" />
                <a href="${loginUrl}" class="auth-link">
                    <spring:message code="label.already.have.an.account"/>
                </a>
            </div>
        </div>
    </div>
</body>
</html>