<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <spring:message var="pageTitle" code="page.title.song" />
  <jsp:include page="/WEB-INF/jsp/components/head.jsp">
    <jsp:param name="title" value="${pageTitle}"/>
  </jsp:include>

</head>
<body>
  <div class="main-container">
    <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
      <jsp:param name="loggedUserImgId" value="${loggedUser.image.id}"/>
      <jsp:param name="moderator" value="${loggedUser.moderator}"/>
    </jsp:include>

    <main class="content-wrapper">
      <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert">
          <strong><spring:message code="message.error"/>:</strong>
          <c:out value="${error}"/>
        </div>
      </c:if>

      <!-- Song Header -->
      <section class="entity-header">
        <div class="entity-main-info">
          <c:url var="songImgUrl" value="/images/${album.image.id}"/>
          <img src="${songImgUrl}" alt="${song.title}" class="entity-image album-cover">
          <div class="entity-details">
            <div class="entity-type">
              <spring:message code="label.song"/>
              <c:if test="${loggedUser.moderator}">
                <c:url var="editSongUrl" value="/mod/edit/song/${song.id}"/>
                <a href="${editSongUrl}" class="edit-link">
                  <i class="fas fa-pencil-alt"></i>
                </a>
              </c:if>
            </div>
            <h1 class="entity-title"><c:out value="${song.title}"/></h1>
            
            <div class="song-metadata">
              <!-- Artists -->
              <div class="artists-list">
                <c:forEach var="artist" items="${artists}">
                  <c:url var="artistUrl" value="/artist/${artist.id}" />
                  <a href="${artistUrl}" class="artist-link">
                    <c:url var="artistImgUrl" value="/images/${artist.image.id}"/>
                    <img src="${artistImgUrl}" alt="${artist.name}" class="artist-thumbnail">
                    <span class="artist-name"><c:out value="${artist.name}"/></span>
                  </a>
                </c:forEach>
              </div>

              <!-- Album -->
              <div class="album-link-container">
                <c:url var="albumUrl" value="/album/${album.id}"/>
                <a href="${albumUrl}" class="album-link">
                  <c:url var="albumImgUrl" value="/images/${album.image.id}"/>
                  <img src="${albumImgUrl}" alt="${album.title}" class="album-thumbnail">
                  <span class="album-name"><c:out value="${album.title}"/></span>
                </a>
              </div>
            </div>
          </div>
        </div>

        <!-- Rating Card -->
        <spring:message code="label.song" var="entityType"/>
        <div class="rating-card-container">
          <jsp:include page="/WEB-INF/jsp/components/rating_card.jsp">
            <jsp:param name="totalRatings" value="${song.ratingCount}"/>
            <jsp:param name="averageRating" value="${song.avgRating}"/>
            <jsp:param name="userRating" value="${loggedUserRating}"/>
            <jsp:param name="reviewed" value="${isReviewed}"/>
            <jsp:param name="entityType" value="${entityType}"/>
            <jsp:param name="entityId" value="${songId}"/>
          </jsp:include>
        </div>
      </section>

      <!-- Action Buttons -->
      <section class="entity-actions">
        <c:url value="/song/${song.id}/add-favorite" var="add_favorite_url" />
        <c:url value="/song/${song.id}/remove-favorite" var="remove_favorite_url" />
        <c:url value="/user/login" var="login" />
        <c:choose>
            <c:when test="${loggedUser.id == 0}">
                <a href="${login}" class="btn btn-primary">
                  <spring:message code="label.login.favorite"/>
                </a>
            </c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${!isFavorite}">
                        <a href="${add_favorite_url}" class="btn btn-primary">
                            <spring:message code="button.add.favorites"/>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${remove_favorite_url}" class="btn btn-secondary">
                            <spring:message code="button.remove.favorites"/>
                        </a>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>
      </section>

      <!-- Song Details -->
      <section class="entity-section song-details">
        <div class="song-info-grid">
          <div class="song-info-item">
            <span class="info-label"><spring:message code="label.duration"/>:</span>
            <span class="info-value"><c:out value="${song.duration}"/></span>
          </div>
          <div class="song-info-item">
            <span class="info-label"><spring:message code="label.genre"/>:</span>
            <span class="info-value"><c:out value="${song.album.genre}"/></span>
          </div>
          <div class="song-info-item">
            <span class="info-label"><spring:message code="label.release.date"/>:</span>
            <span class="info-value"><c:out value="${song.album.formattedReleaseDate}"/></span>
          </div>
        </div>
      </section>

      <!-- Reviews Section -->
      <c:if test="${reviews.size() > 0}">
        <section class="entity-section">
          <h2><spring:message code="label.reviews"/></h2>
          <div class="reviews-grid">
            <c:forEach var="review" items="${reviews}">
              <jsp:include page="/WEB-INF/jsp/components/review_card.jsp">
                <jsp:param name="item_img_id" value="${review.song.album.image.id}"/>
                <jsp:param name="item_name" value="${review.song.title}"/>
                <jsp:param name="item_url" value="/song/${review.song.id}"/>
                <jsp:param name="item_type" value="Song"/>
                <jsp:param name="title" value="${review.title}"/>
                <jsp:param name="rating" value="${review.rating}"/>
                <jsp:param name="review_content" value="${review.description}"/>
                <jsp:param name="user_name" value="@${review.user.username}"/>
                <jsp:param name="user_img_id" value="${review.user.image.id}"/>
                <jsp:param name="verified" value="${review.user.verified}"/>
                <jsp:param name="moderator" value="${loggedUser.moderator}"/>
                <jsp:param name="userModerator" value="${review.user.moderator}"/>
                <jsp:param name="likes" value="${review.likes}"/>
                <jsp:param name="user_id" value="${review.user.id}"/>
                <jsp:param name="review_id" value="${review.id}"/>
                <jsp:param name="isLiked" value="${review.liked}"/>
                <jsp:param name="commentAmount" value="${review.commentAmount}"/>
                <jsp:param name="timeAgo" value="${review.timeAgo}"/>
              </jsp:include>
            </c:forEach>
          </div>

          <!-- Pagination -->
          <div class="pagination">
            <c:url value="/song/${songId}?pageNum=${pageNum + 1}" var="nextPage" />
            <c:url value="/song/${songId}?pageNum=${pageNum - 1}" var="prevPage" />
            <c:if test="${showPrevious}">
              <a href="${prevPage}" class="btn btn-secondary">
                <spring:message code="button.previous.page"/>
              </a>
            </c:if>
            <c:if test="${showNext}">
              <a href="${nextPage}" class="btn btn-secondary">
                <spring:message code="button.next.page"/>
              </a>
            </c:if>
          </div>
        </section>
      </c:if>

      <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
    </main>
  </div>
</body>
</html>
