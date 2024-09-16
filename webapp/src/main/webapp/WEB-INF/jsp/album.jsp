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
            <c:url value="/album/${album.id}/add-favorite" var="add_favorite_url" />
            <c:url value="/album/${album.id}/remove-favorite" var="remove_favorite_url" />
            <c:choose>
              <c:when test="${!isFavorite}">
                <a href="${add_favorite_url}">
                  <button type="submit">Add to favorites</button>
                </a>
              </c:when>
              <c:otherwise>
                <a href="${remove_favorite_url}" >
                  <button type="submit">Remove from favorites</button>
                </a>
              </c:otherwise>
            </c:choose>
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
            <jsp:param name="item_img_id" value="${review.album.imgId}"/>
            <jsp:param name="item_name" value="${review.album.title}"/>
            <jsp:param name="item_url" value="/album/${review.album.id}"/>
            <jsp:param name="artist_url" value="/artist/${review.album.artist.id}"/>
            <jsp:param name="item_type" value="${review.album.artist.name} - Album"/>
            <jsp:param name="title" value="${review.title}"/>
            <jsp:param name="rating" value="${review.rating}"/>
            <jsp:param name="review_content" value="${review.description}"/>
            <jsp:param name="user_name" value="${review.user.name}"/>
            <jsp:param name="user_img_id" value="${review.user.imgId}"/>
            <jsp:param name="likes" value="${review.likes}"/>
            <jsp:param name="user_id" value="${review.user.id}"/>
          </jsp:include>
        </c:forEach>
      </div>
    </div>
  </body>
</html>

