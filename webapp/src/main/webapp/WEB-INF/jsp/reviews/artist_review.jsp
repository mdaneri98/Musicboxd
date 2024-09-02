<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Review Artist</title>

    <!-- <link rel="stylesheet" href="/css/base.css"> -->
    <c:url var="artist_review_css" value="/static/css/artist_review.css"/>
    <link rel="stylesheet" href="${artist_review_css}">

</head>
<body>
<div class="container">
    <h1>Review Artist</h1>

    <c:url value="/artist/${artist.id}" var="artistUrl" />
    <a href="${artistUrl}" class="artist-box">
        <img src="/webapp_war/images/${artist.imgId}" alt="${artist.name}" class="artist-image">
        <div class="artist-info">
            <h2 class="artist-name">${artist.name}</h2>
            <p class="artist-bio">${artist.bio}</p>
        </div>
    </a>

    <c:url var="posturl" value="/artist/${artist.id}/reviews" />
    <form:form modelAttribute="artistReviewForm" action="${posturl}" method="POST" class="review-form">
        <div class="form-group">
            <label for="userId">Tu ID:</label>
            <form:input path="userId" id="userId" type="text" />
            <form:errors path="userId" cssClass="error" />
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