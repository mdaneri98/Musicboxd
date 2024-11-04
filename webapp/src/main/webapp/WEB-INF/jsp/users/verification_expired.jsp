<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <spring:message var="pageTitle" code="page.verification.title"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${pageTitle}"/>
    </jsp:include>
</head>
<body>
    <div class="main-container">
        <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
            <jsp:param name="loggedUserImgId" value="${loggedUser.image.id}"/>
            <jsp:param name="moderator" value="${loggedUser.moderator}"/>
            <jsp:param name="unreadNotificationCount" value="${loggedUser.unreadNotificationCount}"/>
        </jsp:include>

        <main class="content-wrapper">
            <div class="error-page">
                <div class="error-content">
                    <h1 class="error-title">
                        <spring:message code="label.verification.expired" />
                    </h1>
                    <c:url var="homeUrl" value="/" />
                    <a href="${homeUrl}" class="btn btn-primary">
                        <spring:message code="label.return" />
                    </a>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
