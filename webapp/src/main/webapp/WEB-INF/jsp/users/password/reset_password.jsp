<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
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

    <form:form modelAttribute="resetPasswordForm" action="${url}" method="post">
        <c:url var="posturl" value="/user/reset-password?code=${resetPasswordForm.code}" />
        <div class="form-group">
            <label> <spring:message code="label.password"/> <form:input path="password" type="password"/></label>
            <form:errors path="password" element="p" cssStyle="color:red;"/>
        </div>
        <div class="form-group">
            <label> <spring:message code="label.repeat.password"/><form:input path="repeatPassword" type="password"/></label>
            <form:errors path="repeatPassword" cssStyle="color:error;" element="p"/>
        </div>
        <div class="form-group">
            <form:errors path="" element="p" cssStyle="color:red;" />
        </div>
        <button type="submit" class="button"><spring:message code="label.register"/></button>
    </form:form>
</div>

</body>
</html>