<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="error.four.cero.four.title"/></title>
    <!--<link rel="stylesheet" href="<c:url value='/static/css/style.css'/>">-->
    <style>
        .error-container {
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            text-align: center;
            padding: 2rem;
            background-color: #f8f9fa;
        }

        .error-code {
            font-size: 8rem;
            font-weight: bold;
            color: #343a40;
            margin: 0;
            line-height: 1;
        }

        .error-divider {
            width: 100px;
            height: 4px;
            background-color: #3498db;
            margin: 2rem auto;
            border-radius: 2px;
        }

        .error-message {
            font-size: 1.5rem;
            color: #495057;
            margin: 1rem 0;
            max-width: 600px;
        }

        .error-actions {
            margin-top: 2rem;
        }

        .error-button {
            display: inline-block;
            padding: 1rem 2rem;
            background-color: #3498db;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            transition: background-color 0.3s;
            font-weight: 500;
            margin: 0.5rem;
        }

        .error-button:hover {
            background-color: #2980b9;
        }

        .error-suggestion {
            margin-top: 2rem;
            color: #6c757d;
            font-size: 1.1rem;
        }
    </style>
</head>
<body>
<div class="error-container">
    <h1 class="error-code">404</h1>
    <div class="error-divider"></div>
    <h2 class="error-message"></h2>
    <p class="error-suggestion"><spring:message code="error.four.cero.four.title" /></p>
    <div class="error-actions">
        <a href="<c:url value='/'/>" class="error-button"><spring:message code="message.go.start" /></a>
        <a href="javascript:history.back()" class="error-button" style="background-color: #6c757d;"><spring:message code="message.go.back" /></a>
    </div>
</div>
</body>
</html>