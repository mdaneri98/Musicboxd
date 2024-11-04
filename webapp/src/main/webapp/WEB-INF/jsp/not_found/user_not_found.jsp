<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isErrorPage="true" contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>User Not Found</title>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp"/>
</head>
<body>
    <div class="main-container">
        <main class="content-wrapper">
            <div class="error-page">
                <div class="error-content">
                    <h1 class="error-code">404</h1>
                    <h2 class="error-title">
                        <spring:message code="error.user.not.found"/>
                    </h2>
                    <p class="error-message">
                        <spring:message code="error.user.not.found.message"/>
                    </p>
                    <c:url var="homeUrl" value="/"/>
                    <a href="${homeUrl}" class="btn btn-primary">
                        <spring:message code="error.back.to.home"/>
                    </a>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
