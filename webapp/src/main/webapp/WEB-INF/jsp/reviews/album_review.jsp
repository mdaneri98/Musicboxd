<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>

    <spring:message var="pageTitle" text="Album Review"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${pageTitle}"/>
    </jsp:include>

    <c:url var="css" value="/static/css/artist_review.css" />
    <link rel="stylesheet" href="${css}">

</head>
<body>
<div class="container">
    <h1>Make a review</h1>

    <c:url value="/album/${album.id}" var="albumUrl" />
    <a href="${albumUrl}" class="artist-box">
        <c:url var="imgUrl" value="/images/${album.imgId}"/>
        <img src="${imgUrl}" alt="${album.title}" class="artist-image">
        <div class="artist-info">
            <h2 class="artist-name">${album.title}</h2>
            <p class="artist-bio">${album.releaseDate}</p>
        </div>
    </a>

    <c:url var="posturl" value="/album/${album.id}/reviews" />
    <jsp:include page="/WEB-INF/jsp/components/review_form.jsp">
        <jsp:param name="modelAttribute" value="albumReviewForm"/>
        <jsp:param name="posturl" value="${posturl}"/>
    </jsp:include>
</div>
</body>
</html>