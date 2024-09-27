<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<title>${param.title}</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<c:url var="logoUrl" value="/static/assets/logo.png"/>
<link rel="icon" type="image/x-icon" href="${logoUrl}">
<c:url var="cssUrl" value="/static/css/base.css"/>
<link rel="stylesheet" href="${cssUrl}">

<!-- jQuery desde CDN -->
<script src="https://code.jquery.com/jquery-3.7.1.slim.js" integrity="sha256-UgvvN8vBkgO0luPSUl2s8TIlOSYRoGFAX4jlCIm9Adc=" crossorigin="anonymous"></script>
