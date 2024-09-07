<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <spring:message var="pageTitle" text="Submit an Album"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${pageTitle}"/>
    </jsp:include>

    <c:url var="css" value="/static/css/artist_review.css" />
    <link rel="stylesheet" href="${css}">

</head>
<body>
<div class="container">
    <h1>Submit an Album</h1>

    <c:url var="postUrl" value="/artist/${artistId}/mod/add/album" />
    <form action="${postUrl}" method="post" enctype="multipart/form-data">
        <div>
            <label>Title:
                <input name="title" type="text" />
                <form:errors path="title" cssClass="error" />
            </label>
        </div>
        <div>
            <label>Genre:
                <input name="genre" type="text" />
                <form:errors path="genre" cssClass="error" />
            </label>
        </div>
        <!--
        <div>
            <label>Release Date:
                <input name="releaseDate" type="datetime-local" />
                form:errors path="releaseDate" cssClass="error" />
            </label>
        </div>
        -->
        <div>
            <label>Image:
                <input name="file" type="file" />
            </label>
        </div>
        <div>
            <button type="submit">Submit Album</button>
        </div>
    </form>
</div>
</body>
</html>