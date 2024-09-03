<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reseña</title>

    <!-- <link rel="stylesheet" href="/css/base.css"> -->
    <c:url var="artist_review_css" value="/static/css/artist_review.css"/>
    <link rel="stylesheet" href="${artist_review_css}">

</head>
<body>
<div class="container">
    <h1><c:out value="${album.title}" /></h1>

    <c:url value="/album/${album.id}" var="albumUrl" />
    <a href="${albumUrl}" class="album-box">
        <img src="/webapp_war/images/${album.imgId}" alt="${album.title}" class="album-image">
        <div class="album-info">
            <h2 class="album-name">${album.title}</h2>
            <p class="album-bio">${album.releaseDate}</p>
        </div>
    </a>

    <c:url var="posturl" value="/album/${album.id}/reviews" />
    <form:form modelAttribute="albumReviewForm" action="${posturl}" method="POST" class="review-form">
        <div class="form-group">
            <label for="userEmail">Tu email:</label>
            <form:input path="userEmail" id="userEmail" type="text" />
            <form:errors path="userEmail" cssClass="error" />
        </div>
        <div class="form-group">
            <label for="title">Título:</label>
            <form:input path="title" id="title" type="text" />
            <form:errors path="title" cssClass="error" />
        </div>
        <div class="form-group">
            <label for="description">Descripción:</label>
            <form:textarea path="description" id="description" rows="4" />
            <form:errors path="description" cssClass="error" />
        </div>
        <div class="form-group star-rating">
            <label for="rating">Rating:</label>
            <form:input path="rating" id="rating" type="text" />
            <form:errors path="rating" cssClass="error" />
        </div>
        <div class="form-group">
            <button type="submit" class="btn btn-submit">Submit Review</button>
        </div>
    </form:form>
</div>
</body>
</html>