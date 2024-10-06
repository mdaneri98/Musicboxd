<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <spring:message var="pageTitle" code="page.artist.review.title"/>
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
<div class="main-content container">
    <h1><spring:message code="label.make.a.review" /></h1>

    <c:url value="/artist/${artist.id}" var="artistUrl" />
    <a href="${artistUrl}" class="artist-box">
        <c:url var="imgUrl" value="/images/${artist.imgId}"/>
        <img src="${imgUrl}" alt="${artist.name}" class="primary-image">
        <div class="artist-info">
            <h2 class="artist-name">${artist.name}</h2>
            <p class="artist-bio">${artist.bio}</p>
        </div>
    </a>

    <c:choose>
        <c:when test="${!edit}">
            <c:url var="posturl" value="/artist/${artist.id}/reviews" />
            <jsp:include page="/WEB-INF/jsp/components/review_form.jsp">
                <jsp:param name="posturl" value="${posturl}"/>
            </jsp:include>
        </c:when>
        <c:otherwise>
            <c:url var="posturl" value="/artist/${artist.id}/edit-review" />
            <jsp:include page="/WEB-INF/jsp/components/review_form.jsp">
                <jsp:param name="posturl" value="${posturl}"/>
            </jsp:include>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>