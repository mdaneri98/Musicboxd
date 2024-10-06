<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <spring:message var="pageTitle" code="submit.song.title"/>
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
        <jsp:param name="moderator" value="${loggedUser.moderator}"/>
    </jsp:include>
</div>
<div class="container">
    <h1><spring:message code="submit.song.title" /></h1>

    <c:url var="postURL" value="${postUrl}" />
    <form:form modelAttribute="modSongForm" action="${postURL}" method="post">
        <input name="id" type="hidden" value="${modSongForm.id}"/>
        <input name="albumId" type="hidden" value="${modSongForm.albumId}"/>
        <div>
            <label><spring:message code="submit.song.title.label" />
                <form:errors path="title" cssClass="error" element="p" cssStyle="color:red;"/>
                <form:input path="title" type="text" />
            </label>
        </div>
        <div>
            <label><spring:message code="submit.song.duration.label" />
                <form:errors path="duration" cssClass="error" element="p" cssStyle="color:red;"/>
                <form:input path="duration" type="text" placeholder="MM:SS - Example: 10:24 or 3:15" />
            </label>
        </div>
        <div>
            <label><spring:message code="submit.song.track.number.label" />
                <form:errors path="trackNumber" cssClass="error" element="p" cssStyle="color:red;"/>
                <form:input path="trackNumber" type="number" />
            </label>
        </div>
        <div style="display: flex">
            <!-- Cancel Button -->
            <c:if test="${songId == null}">
                <c:url value="/mod" var="cancel_url" />
            </c:if>
            <c:if test="${songId != null}">
                <c:url value="/song/${songId}" var="cancel_url" />
            </c:if>
            <a href="${cancel_url}">
                <button type="button">Cancel</button>
            </a>

            <!-- Submit Button -->
            <button type="submit" style="margin-left: auto"><spring:message code="submit.song.button" /></button>
        </div>
    </form:form>
</div>
</body>
</html>