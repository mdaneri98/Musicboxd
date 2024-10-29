<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>404 - Not Found</title>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp"/>
</head>
<body>
    <div class="main-container">
        <main class="content-wrapper">
            <div class="error-page">
                <div class="error-content">
                    <h1 class="error-code">404</h1>
                    <h2 class="error-title"><spring:message code="error.404.header"/></h2>
                    <p class="error-message"><spring:message code="error.404.description"/></p>
                    <c:url var="homeUrl" value="/" />
                    <a href="${homeUrl}" class="btn btn-primary">
                        <spring:message code="error.back.to.home"/>
                    </a>
                </div>
            </div>
        </main>
    </div>
</body>
</html>