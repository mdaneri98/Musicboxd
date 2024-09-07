<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <spring:message var="pageTitle" text="Album Page"/>
  <jsp:include page="/WEB-INF/jsp/components/head.jsp">
    <jsp:param name="title" value="${pageTitle}"/>
  </jsp:include>

  <c:url var="css" value="/static/css/album.css" />
  <link rel="stylesheet" href="${css}">
</head>
  <body>

    <div class="container">
      <div class="album-header">
        <c:url var="albumImgUrl" value="/images/${album.imgId}"/>
        <img src="${albumImgUrl}" alt="${album.title}" class="album-image">
        <div class="album-info">
          <p class="album-type">Album</p>
          <h1><c:out value="${album.title}"/></h1>
          <div class="button-group">
            <c:url var="artistUrl" value="/artist/${artist.id}" />
            <a href="${artistUrl}" class="button artist-button">
              <c:url var="artistImgUrl" value="/images/${artist.imgId}"/>
              <img src="${artistImgUrl}" alt="${artist.name}" class="artist-image">
              <span><c:out value="${artist.name}"/></span>
            </a>
            <c:url var="albumReviewUrl" value="/album/${album.id}/reviews" />
            <a href="${albumReviewUrl}" class="button review-button">Make a review</a>
            </a>
            <c:url var="NewAlbumUrl" value="/album/${album.id}/mod/add/song" />
            <a href="${NewAlbumUrl}" class="button review-button">Add Song</a>
          </div>
        </div>
      </div>

      <ul class="song-list">
        <c:forEach var="song" items="${songs}" varStatus="status">
          <li>
            <span class="song-number">${status.index + 1}</span>
            <c:url var="songUrl" value="/song/${song.id}" />
            <a href="${songUrl}" class="song-title"><c:out value="${song.title}"/></a>
          </li>
        </c:forEach>
      </ul>

      <!-- Cards Container -->
      <div class="cards-container">
        <c:forEach var="review" items="${reviews}">
          <jsp:include page="/WEB-INF/jsp/components/review_card.jsp">
            <jsp:param name="title" value="${review.title}"/>
            <jsp:param name="description" value="${review.description}"/>
            <jsp:param name="userId" value="${review.userId}"/>
            <jsp:param name="imgId" value="${album.imgId}"/>
          </jsp:include>
        </c:forEach>
      </div>
    </div>
  </body>
</html>

