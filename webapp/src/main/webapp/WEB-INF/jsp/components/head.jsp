<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<title><c:out value="${param.title}"/></title>
<meta charset="UTF-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>

<!-- Favicon -->
<c:url var="logoUrl" value="/static/assets/logo.png"/>
<link rel="icon" type="image/x-icon" href="${logoUrl}">

<!-- Font Awesome -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">

<!-- jQuery -->
<script src="https://code.jquery.com/jquery-3.7.1.slim.js" integrity="sha256-UgvvN8vBkgO0luPSUl2s8TIlOSYRoGFAX4jlCIm9Adc=" crossorigin="anonymous"></script>

<!-- Custom CSS -->
<c:url var="baseCss" value="/static/css/base.css"/>
<c:url var="layoutCss" value="/static/css/layout.css"/>
<c:url var="componentsCss" value="/static/css/components.css"/>
<c:url var="modulesCss" value="/static/css/modules.css"/>

<link rel="stylesheet" href="${baseCss}">
<link rel="stylesheet" href="${layoutCss}">
<link rel="stylesheet" href="${componentsCss}">
<link rel="stylesheet" href="${modulesCss}">

<!-- Mover el script de tema aquí para que se cargue en todas las páginas -->
<script src="<c:url value='/static/js/theme.js'/>"></script>

<c:if test="${loggedUser.id != 0}">
    <c:set var="userTheme" value="${loggedUser.theme}"/>
</c:if>
<body data-user-theme="${userTheme}">