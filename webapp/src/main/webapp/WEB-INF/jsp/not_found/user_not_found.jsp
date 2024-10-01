<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page isErrorPage="true" contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Page Not Found</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/404.css">
</head>
<body>
<div class="error-container">
    <h1><spring:message code="error.user.not.found" /></h1>
    <p><spring:message code="error.user.not.found.message" /></p>
    <p><spring:message code="error.back.to.home" /> <a href="${pageContext.request.contextPath}/"><spring:message code="error.back.to.home" /></a>.</p>
</div>

</body>
</html>
