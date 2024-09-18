<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <spring:message var="pageTitle" text="Edit Profile"/>
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
    <h1>Edit Profile</h1>

    <c:url var="editProfileUrl" value="/user/edit" />
    <form:form modelAttribute="userProfileForm" action="${editProfileUrl}" method="post" enctype="multipart/form-data">
        <div>
            <label>Username:
                <form:errors path="username" cssClass="error" element="p" cssStyle="color:red;"/>
                <form:input path="username" type="text" />
            </label>
        </div>
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
            <label>Profile Picture:
                <form:errors path="profilePicture" cssClass="error" element="p" cssStyle="color:red;"/>
                <form:input path="profilePicture" type="file" accept=".jpg,.jpeg,.png" />
            </label>
        </div>
        <div style="display: flex; gap: 10px;">
            <c:url value="/user/" var="discard_changes_url" />
            <a href="${discard_changes_url}" style="flex: 1;">
                <button type="button" style="width: 100%; height: 100%;">Discard Changes</button>
            </a>
            <button type="submit" style="flex: 1; width: 100%; height: 100%;">Update</button>
        </div>
    </form:form>
</div>
</body>
</html>
