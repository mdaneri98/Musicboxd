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
<div>
    <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
        <jsp:param name="loggedUserImgId" value="${loggedUser.imgId}"/>
    </jsp:include>
</div>
<div class="container">
    <h1>Submit an Artist</h1>

    <c:url var="postUrl" value="/mod/add/artist" />
    <form:form modelAttribute="modArtistForm" action="${postUrl}" method="post" enctype="multipart/form-data">
        <div>
            <label>Name:
                <form:errors path="name" cssClass="error" element="p" cssStyle="color:red;"/>
                <form:input path="name" type="text" />
            </label>
        </div>
        <div>
            <label>Bio:
                <form:errors path="bio" cssClass="error" element="p" cssStyle="color:red;"/>
                <form:input path="bio" type="text" />
            </label>
        </div>
        <div>
            <label>Image:
                <form:errors path="file" cssClass="error" element="p" cssStyle="color:red;"/>
                <form:input path="file" type="file" accept=".jpg,.jpeg,.png" />
            </label>
        </div>
        <div>
            <button type="submit">Submit Artist</button>
        </div>
    </form:form>
</div>
</body>
</html>