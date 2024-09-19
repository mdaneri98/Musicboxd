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
<div>
    <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
        <jsp:param name="loggedUserImgId" value="${loggedUser.imgId}"/>
    </jsp:include>
</div>
<div class="container">
    <h1>Submit a Song</h1>

    <c:url var="postUrl" value="/mod/add/album/${albumId}/song" />
    <form:form modelAttribute="modSongForm" action="${postUrl}" method="post">
        <div>
            <label>Title:
                <form:errors path="title" cssClass="error" element="p" cssStyle="color:red;"/>
                <form:input path="title" type="text" />
            </label>
        </div>
        <div>
            <label>Duration:
                <form:errors path="duration" cssClass="error" element="p" cssStyle="color:red;"/>
                <form:input path="duration" type="text" placeholder="MM:SS - Example: 10:24 or 3:15" />
            </label>
        </div>
        <div>
            <label>Track Number:
                <form:errors path="trackNumber" cssClass="error" element="p" cssStyle="color:red;"/>
                <form:input path="trackNumber" type="number" />
            </label>
        </div>
        <div>
            <button type="submit">Submit Song</button>
        </div>
    </form:form>
</div>
</body>
</html>