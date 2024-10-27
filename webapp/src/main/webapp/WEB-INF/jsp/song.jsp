<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <spring:message var="pageTitle" code="page.title.song" />
  <jsp:include page="/WEB-INF/jsp/components/head.jsp">
    <jsp:param name="title" value="${pageTitle}"/>
  </jsp:include>

  <c:url var="review_card" value="/static/css/review_card.css" />
  <link rel="stylesheet" href="${review_card}">

</head>
<body>
  <div>
    <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
      <jsp:param name="loggedUserImgId" value="${loggedUser.image.id}"/>
      <jsp:param name="moderator" value="${loggedUser.moderator}"/>
    </jsp:include>
  </div>

  <section class="container">
    <div>
      <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
          <strong><spring:message code="message.error"/>:</strong>
          <c:out value="${error}"/>
        </div>
      </c:if>
    </div>
    <div class="info-container">
      <div>
        <c:url var="songImgUrl" value="/images/${album.imgId}"/>
        <img src="${songImgUrl}" alt="${song.title}" class="album">
      </div>
      <div class="data-container">
        <p class="type"><spring:message code="label.song"/>
          <c:if test="${loggedUser.moderator}">
          <c:url var="editSongUrl" value="/mod/edit/song/${song.id}"/>
          <a href="${editSongUrl}">
            <i class="fas fa-pencil-alt"></i>
          </a>
          </c:if>
        </p>
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
        </div>
        <div class="artist-album-container">
        <c:url var="albumUrl" value="/album/${album.id}"/>
        <a href="${albumUrl}" class="artist-button">
          <c:url var="albumImgUrl" value="/images/${album.imgId}"/>
          <img src="${albumImgUrl}" alt="${album.title}" class="primary-image">
          <span><c:out value="${album.title}"/></span>
        </a>
      </div>
      </div>
      <div class="rating-card-container">
        <jsp:include page="/WEB-INF/jsp/components/rating_card.jsp">
          <jsp:param name="totalRatings" value="${song.ratingCount}"/>
          <jsp:param name="averageRating" value="${song.avgRating}"/>
          <jsp:param name="userRating" value="${loggedUserRating}"/>
          <jsp:param name="reviewed" value="${isReviewed}"/>
          <jsp:param name="entityType" value="song"/>
          <jsp:param name="entityId" value="${songId}"/>
        </jsp:include>
      </div>
    </div>
    <div class="button-group">
      <c:url value="/song/${song.id}/add-favorite" var="add_favorite_url" />
      <c:url value="/song/${song.id}/remove-favorite" var="remove_favorite_url" />
      <c:choose>
        <c:when test="${!isFavorite}">
          <a href="${add_favorite_url}">
            <button type="submit"><spring:message code="button.add.favorites"/></button>
          </a>
        </c:when>
        <c:otherwise>
          <a href="${remove_favorite_url}"><button><spring:message code="button.remove.favorites"/></button></a>
        </c:otherwise>
      </c:choose>
    </div>

    <div class="song-description">
      <p><spring:message code="label.duration"/>: <c:out value="${song.duration}"/></p>
      <p><spring:message code="label.genre"/>: <c:out value="${song.album.genre}"/></p>
      <p><spring:message code="label.release.date"/>: <c:out value="${song.album.releaseDate}"/></p>
    </div>

    <c:if test="${reviews.size() > 0}">
    <section>
      <h2><spring:message code="label.reviews"/></h2>
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
              <jsp:param name="moderator" value="${loggedUser.moderator}"/>
              <jsp:param name="userModerator" value="${review.user.moderator}"/>
              <jsp:param name="likes" value="${review.likes}"/>
              <jsp:param name="user_id" value="${review.user.id}"/>
              <jsp:param name="review_id" value="${review.id}"/>
              <jsp:param name="isLiked" value="${review.liked}"/>
            </jsp:include>
          </c:forEach>
        </div>
    </section>
    <section>
      <footer>
        <div class="pages">
          <c:url value="/album/${song.id}?pageNum=${pageNum + 1}" var="nextPage" />
          <c:url value="/album/${song.id}?pageNum=${pageNum -1}" var="prevPage" />
          <c:if test="${showPrevious}"><a href="${prevPage}"><button><spring:message code="button.previous.page"/></button></a></c:if>
          <c:if test="${showNext}"><a href="${nextPage}"><button><spring:message code="button.next.page"/></button></a></c:if>
        </div>
      </footer>
    </section>
  </section>
  </c:if>
</body>
</html>