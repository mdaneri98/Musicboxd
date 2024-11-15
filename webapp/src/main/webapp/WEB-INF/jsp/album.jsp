<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <spring:message code="page.title.album" var="pageTitle"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${pageTitle}"/>
    </jsp:include>
</head>
<body>
    <div class="main-container">
        <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
            <jsp:param name="loggedUserImgId" value="${loggedUser.image.id}"/>
            <jsp:param name="moderator" value="${loggedUser.moderator}"/>
            <jsp:param name="unreadNotificationCount" value="${loggedUser.unreadNotificationCount}"/>
        </jsp:include>

        <main class="content-wrapper">
            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert">
                    <strong><spring:message code="message.error"/>:</strong>
                    <c:out value="${error}"/>
                </div>
            </c:if>

            <!-- Album Header -->
            <section class="entity-header">
                <div class="entity-main-info">
                    <c:url var="albumImgUrl" value="/images/${album.image.id}"/>
                    <img src="${albumImgUrl}" alt="<c:out value="${album.title}" />" class="entity-image album-cover">
                    <div class="entity-details">
                        <div class="entity-type">
                            <spring:message code="label.album"/>
                            <c:if test="${loggedUser.moderator}">
                                <c:url var="editAlbumUrl" value="/mod/edit/album/${album.id}"/>
                                <a href="${editAlbumUrl}" class="edit-link">
                                    <i class="fas fa-pencil-alt"></i>
                                </a>
                            </c:if>
                        </div>
                        <h1 class="entity-title"><c:out value="${album.title}"/></h1>
                        
                        <div class="album-metadata">
                            <c:url var="artistUrl" value="/artist/${artist.id}" />
                            <a href="${artistUrl}" class="artist-link">
                                <c:url var="artistImgUrl" value="/images/${artist.image.id}"/>
                                <img src="${artistImgUrl}" alt="<c:out value="${artist.name}" />" class="artist-thumbnail">
                                <span class="artist-name"><c:out value="${artist.name}"/></span>
                            </a>
                            <div class="album-info">
                                <span class="album-genre"><c:out value="${album.genre}"/></span>
                                <span class="info-separator">&bull;</span>
                                <span class="album-date"><c:out value="${album.formattedReleaseDate}"/></span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Rating Card -->
                <spring:message code="label.album.lower.case" var="entityLabel"/>
                <div class="rating-card-container">
                    <jsp:include page="/WEB-INF/jsp/components/rating_card.jsp">
                        <jsp:param name="totalRatings" value="${album.ratingCount}"/>
                        <jsp:param name="averageRating" value="${album.avgRating}"/>
                        <jsp:param name="userRating" value="${loggedUserRating}"/>
                        <jsp:param name="reviewed" value="${isReviewed}"/>
                        <jsp:param name="entityType" value="album"/>
                        <jsp:param name="entityLabel" value="${entityLabel}"/>
                        <jsp:param name="entityId" value="${albumId}"/>
                    </jsp:include>
                </div>
            </section>

            <!-- Action Buttons -->
            <section class="entity-actions">
                <c:url value="/album/${album.id}/add-favorite" var="add_favorite_url" />
                <c:url value="/album/${album.id}/remove-favorite" var="remove_favorite_url" />
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

            <!-- Songs Section -->
            <c:if test="${songs.size() > 0}">
                <section class="entity-section">
                    <h2><spring:message code="label.songs"/></h2>
                    <ul class="song-list">
                        <c:forEach var="song" items="${songs}" varStatus="status">
                            <c:url var="songUrl" value="/song/${song.id}"/>
                            <li>
                                <a href="${songUrl}" class="song-item">
                                    <span class="song-number">${status.index + 1}</span>
                                    <span class="song-title"><c:out value="${song.title}"/></span>
                                    <div class="rating-badge">
                                        <fmt:formatNumber value="${song.avgRating}" maxFractionDigits="1" var="formattedRating"/>
                                        <span class="rating"><c:out value="${formattedRating}"/></span>
                                        <span class="star">&#9733;</span>
                                    </div>
                                </a>
                            </li>
                        </c:forEach>
                    </ul>
                </section>
            </c:if>

            <!-- Reviews Section -->
            <c:if test="${reviews.size() > 0}">
                <section class="entity-section">
                    <h2><spring:message code="label.reviews"/></h2>
                    <div class="reviews-grid">
                        <c:forEach var="review" items="${reviews}">
                            <jsp:include page="/WEB-INF/jsp/components/review_card.jsp">
                                <jsp:param name="item_img_id" value="${album.image.id}"/>
                                <jsp:param name="item_name" value="${review.album.title}"/>
                                <jsp:param name="item_url" value="/album/${review.album.id}"/>
                                <jsp:param name="item_type" value="Album"/>
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
                                <jsp:param name="blocked" value="${review.isBlocked()}"/>
                                <jsp:param name="commentAmount" value="${review.commentAmount}"/>
                                <jsp:param name="timeAgo" value="${review.timeAgo}"/>
                            </jsp:include>
                        </c:forEach>
                    </div>

                    <!-- Pagination -->
                    <div class="pagination">
                        <c:url value="/album/${albumId}?pageNum=${pageNum + 1}" var="nextPage" />
                        <c:url value="/album/${albumId}?pageNum=${pageNum - 1}" var="prevPage" />
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
