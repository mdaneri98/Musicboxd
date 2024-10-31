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
    <!-- Links -->
    <c:url var="home" value="/"/>
    <c:url var="music" value="/music"/>
    <c:url var="login" value="/user/login"/>
    <c:url var="register" value="/user/register"/>

    <div class="main-container-no-sidebar">
        <header class="landing-header">
            <nav class="nav-bar">
                <div class="logo"><spring:message code="webpage.name"/></div>
                <div class="nav-links">
                    <a href="${home}" class="nav-link"><spring:message code="page.title.home"/></a>
                    <a href="${music}" class="nav-link"><spring:message code="page.title.discovery"/></a>
                    <a href="${login}" class="nav-link"><spring:message code="label.login"/></a>
                    <a href="${register}" class="nav-link"><spring:message code="label.register"/></a>
                </div>
            </nav>
        </header>
        <div class="auth-container">
            <div class="auth-card">
                <h1 class="auth-title"><spring:message code="webpage.name"/></h1>

                <form:form modelAttribute="resetPasswordForm" action="${url}" method="post" class="auth-form">
                    <c:url var="posturl" value="/user/reset-password?code=${resetPasswordForm.code}" />

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
            </div>
        </div>
        <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
    </div>
</body>
</html>