<%--
  Created by IntelliJ IDEA.
  User: manuader
  Date: 26/10/2024
  Time: 2:13 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>500 - Server Error</title>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp"/>
</head>
<body>
    <div class="main-container">
        <main class="content-wrapper">
            <div class="error-page">
                <div class="error-content">
                    <h1 class="error-code">500</h1>
                    <h2 class="error-title"><spring:message code="error.500.header"/></h2>
                    <p class="error-message"><spring:message code="error.500.description"/></p>
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
