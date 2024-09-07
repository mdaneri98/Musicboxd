<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <spring:message var="pageTitle" text="Submit Artist"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${pageTitle}"/>
    </jsp:include>

    <c:url var="css" value="/static/css/artist_review.css" />
    <link rel="stylesheet" href="${css}">

</head>
<body>
<div class="container">
    <h1>Submit an Artist</h1>

    <c:url var="postUrl" value="/mod/add/artist" />
    <form action="${postUrl}" method="post" enctype="multipart/form-data">
        <div>
            <label>Name:
                <input name="name" type="text" />
                <form:errors path="name" cssClass="error" />
            </label>
        </div>
        <div>
            <label>Bio:
            <input name="bio" type="text" />
            <form:errors path="bio" cssClass="error" />
            </label>
        </div>
        <div>
            <label>Image:
                <input name="file" type="file" />
            </label>
        </div>
        <div>
            <button type="submit">Submit Artist</button>
        </div>
    </form>
</div>
</body>
</html>