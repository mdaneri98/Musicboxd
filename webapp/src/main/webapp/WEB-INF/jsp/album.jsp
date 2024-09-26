<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <spring:message var="pageTitle" text="Album Page"/>
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
        <jsp:param name="moderator" value="${loggedUser.moderator}"/>
      </jsp:include>
    </div>
    <div class="container">
      <div class="info-container">
        <c:url var="albumImgUrl" value="/images/${album.imgId}"/>
        <img src="${albumImgUrl}" alt="${album.title}" class="album">
          <div class="data-container">
          <p class="type">Album</p>
          <h1><c:out value="${album.title}"/></h1>
          <div class="button-group">
            <c:url var="artistUrl" value="/artist/${artist.id}" />
            <a href="${artistUrl}" class="button artist-button">
              <c:url var="artistImgUrl" value="/images/${artist.imgId}"/>
              <img src="${artistImgUrl}" alt="${artist.name}" class="secondary-image">
              <span><c:out value="${artist.name}"/></span>
            </a>
          </div>
        </div>
      </div>
        <div class="data-container">
          <c:url var="albumReviewUrl" value="/album/${album.id}/reviews" />
          <a href="${albumReviewUrl}"><button>Make a review</button></a>
          <c:url value="/album/${album.id}/add-favorite" var="add_favorite_url" />
          <c:url value="/album/${album.id}/remove-favorite" var="remove_favorite_url" />
          <c:choose>
            <c:when test="${!isFavorite}">
              <a href="${add_favorite_url}"><button>Add to favorites</button></a>
            </c:when>
            <c:otherwise>
              <a href="${remove_favorite_url}">
                <button>Remove from favorites</button>
              </a>
            </c:otherwise>
          </c:choose>
        </div>

      <div class="song-description">
        <p>Genre: <c:out value="${album.genre}"/></p>
        <p>Release Date: <c:out value="${album.releaseDate}"/></p>
      </div>

      <section id="main-section">
        <c:if test="${songs.size() > 0}">
        <h2>Songs</h2>
        <div>
          <ul class="song-list">
            <c:forEach var="song" items="${songs}" varStatus="status">
              <c:url var="songUrl" value="/song/${song.id}" />
              <a href="${songUrl}" class="song-title">
                <li>
                  <span class="song-number">${status.index + 1}     </span>
                  <span class="song-title"><c:out value="${song.title}"/></span>
                </li>
              </a>
            </c:forEach>
          </ul>
        </div>
        </c:if>

  <c:if test="${reviews.size() > 0}">
      <h2>Reviews</h2>
      <div class="cards-container">
        <c:forEach var="review" items="${reviews}">
          <jsp:include page="/WEB-INF/jsp/components/review_card.jsp">
            <jsp:param name="item_img_id" value="${review.album.imgId}"/>
            <jsp:param name="item_name" value="${review.album.title}"/>
            <jsp:param name="item_url" value="/album/${review.album.id}"/>
            <jsp:param name="item_type" value="Album"/>
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
      </section>
        <section>
            <footer>
                <div>
                  <c:url value="/album/${album.id}/${pageNum + 1}" var="nextPage" />
                  <c:url value="/album/${album.id}/${pageNum -1}" var="prevPage" />
                  <c:if test="${pageNum > 1}"><a href="${prevPage}"><button>Previous page</button></a></c:if>
                  <c:if test="${reviews.size() == 5}"><a href="${nextPage}"><button>Next page</button></a></c:if>
                </div>
            </footer>
        </section>
      </c:if>
    </div>
  </body>
</html>

