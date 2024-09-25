<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>

  <spring:message var="pageTitle" text="${album.title}" />
  <jsp:include page="/WEB-INF/jsp/components/head.jsp">
    <jsp:param name="title" value="${pageTitle}"/>
  </jsp:include>

  <c:url var="review_card" value="/static/css/review_card.css" />
  <link rel="stylesheet" href="${review_card}">

</head>
<body>
  <div>
    <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
      <jsp:param name="loggedUserImgId" value="${loggedUser.imgId}"/>
    </jsp:include>
  </div>

  <div class="container">
    <div class="info-container">
      <div>
        <c:url var="songImgUrl" value="/images/${album.imgId}"/>
        <img src="${songImgUrl}" alt="${song.title}" class="album">
      </div>
      <div class="data-container">
        <p class="type">SONG</p>
        <h1><c:out value="${song.title}"/></h1>
        <div class="artist-album-container">
          <c:forEach var="artist" items="${artists}">
            <c:url var="artistUrl" value="/artist/${artist.id}" />
            <a href="${artistUrl}" class="artist-button">
              <c:url var="artistImgUrl" value="/images/${artist.imgId}"/>
              <img src="${artistImgUrl}" alt="${artist.name}" class="secondary-image">
              <span><c:out value="${artist.name}"/></span>
            </a>
          </c:forEach>
          <c:url var="albumUrl" value="/album/${album.id}"/>
          <a href="${albumUrl}" class="artist-button">
            <c:url var="albumImgUrl" value="/images/${album.imgId}"/>
            <img src="${albumImgUrl}" alt="${album.title}" class="primary-image">
            <span><c:out value="${album.title}"/></span>
          </a>
        </div>
      </div>
    </div>
    <div class="button-group">
      <c:url var="songReviewUrl" value="/song/${song.id}/reviews"/>
      <a href="${songReviewUrl}"><button>Make a review</button></a>
      <c:url value="/song/${song.id}/add-favorite" var="add_favorite_url" />
      <c:url value="/song/${song.id}/remove-favorite" var="remove_favorite_url" />
      <c:choose>
        <c:when test="${!isFavorite}">
          <a href="${add_favorite_url}">
            <button type="submit">Add to favorites</button>
          </a>
        </c:when>
        <c:otherwise>
          <a href="${remove_favorite_url}"><button>Remove from favorites</button></a>
        </c:otherwise>
      </c:choose>
    </div>

    <div class="song-description">
      <p>Duration: <c:out value="${song.duration}"/></p>
      <p>Genre: <c:out value="${song.album.genre}"/></p>
      <p>Release Date: <c:out value="${song.album.releaseDate}"/></p>
    </div>

    <c:if test="${reviews.size() > 0}">
    <h2>Reviews</h2>
    <div class="cards-container">
      <c:forEach var="review" items="${reviews}">
        <jsp:include page="/WEB-INF/jsp/components/review_card.jsp">
          <jsp:param name="item_img_id" value="${review.song.album.imgId}"/>
          <jsp:param name="item_name" value="${review.song.title}"/>
          <jsp:param name="item_url" value="/song/${review.song.id}"/>
          <jsp:param name="item_type" value="Song"/>
          <jsp:param name="title" value="${review.title}"/>
          <jsp:param name="rating" value="${review.rating}"/>
          <jsp:param name="review_content" value="${review.description}"/>
          <jsp:param name="user_name" value="@${review.user.username}"/>
          <jsp:param name="user_img_id" value="${review.user.imgId}"/>
          <jsp:param name="verified" value="${review.user.verified}"/>
          <jsp:param name="moderator" value="${review.user.moderator}"/>
          <jsp:param name="likes" value="${review.likes}"/>
          <jsp:param name="user_id" value="${review.user.id}"/>
          <jsp:param name="review_id" value="${review.id}"/>
          <jsp:param name="isLiked" value="${review.liked}"/>
        </jsp:include>
      </c:forEach>
    </div>
    <c:url value="/song/${song.id}/${pageNum + 1}" var="nextPage" />
    <c:url value="/song/${song.id}/${pageNum -1}" var="prevPage" />
    <c:if test="${pageNum > 1}"><a href="${prevPage}"><button>Previous page</button></a></c:if>
    <c:if test="${reviews.size() == 5}"><a href="${nextPage}"><button>Next page</button></a></c:if>
  </div>
  </c:if>
</body>
</html>