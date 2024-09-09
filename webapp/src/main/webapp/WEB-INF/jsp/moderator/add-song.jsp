<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <spring:message var="pageTitle" text="Submit a Song"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${pageTitle}"/>
    </jsp:include>

    <c:url var="css" value="/static/css/artist_review.css" />
    <link rel="stylesheet" href="${css}">

</head>
<body>
<div class="container">
    <h1>Submit a Song</h1>

    <c:url var="postUrl" value="/album/${albumId}/mod/add/song" />
    <form action="${postUrl}" method="post" enctype="multipart/form-data">
        <div>
            <label>Title:
                <input name="title" type="text" />
                <form:errors path="title" cssClass="error" />
            </label>
        </div>
        <div>
            <label>Duration:
                <input name="duration" type="text" placeholder="MM:SS" />
                <form:errors path="duration" cssClass="error" />
            </label>
        </div>
        <div>
            <label>Track Number:
                <input name="trackNumber" type="number" />
                <form:errors path="trackNumber" cssClass="error" />
            </label>
        </div>
        <div>
            <button type="submit">Submit Song</button>
        </div>
    </form>
</div>
</body>
</html>