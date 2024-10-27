<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <spring:message code="page.title.artist" var="pageTitle"/>
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
<div class="container">
    <div>
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <strong><spring:message code="message.error"/>:</strong>
                <c:out value="${error}"/>
            </div>
        </c:if>
    </div>
    <div class="info-container">
        <c:url var="artistImgURL" value="/images/${artist.image.id}"/>
        <img src="${artistImgURL}" alt="Artist Name" class="primary-image">
        <div class="data-container">
            <p class="type"><spring:message code="label.artist"/>
                <c:if test="${loggedUser.moderator}">
                <c:url var="editArtistUrl" value="/mod/edit/artist/${artist.id}"/>
                <a href="${editArtistUrl}">
                    <i class="fas fa-pencil-alt"></i>
                </a>
            </c:if>
            </p>
            <div>
                <h1><c:out value="${artist.name}"/></h1>
                <p class="artist-bio"><c:out value="${artist.bio}"/></p>
            </div>
        </div>
        <div class="rating-card-container">
            <jsp:include page="/WEB-INF/jsp/components/rating_card.jsp">
                <jsp:param name="totalRatings" value="${artist.ratingCount}"/>
                <jsp:param name="averageRating" value="${artist.avgRating}"/>
                <jsp:param name="userRating" value="${loggedUserRating}"/>
                <jsp:param name="reviewed" value="${isReviewed}"/>
                <jsp:param name="entityType" value="artist"/>
                <jsp:param name="entityId" value="${artistId}"/>
            </jsp:include>
        </div>
    </div>
    <div class="data-container">
        <c:url value="/artist/${artist.id}/add-favorite" var="add_favorite_url" />
        <c:url value="/artist/${artist.id}/remove-favorite" var="remove_favorite_url" />
        <c:choose>
            <c:when test="${!isFavorite}">
                <a href="${add_favorite_url}">
                    <button type="submit"><spring:message code="button.add.favorites"/></button>
                </a>
            </c:when>
            <c:otherwise>
                <a href="${remove_favorite_url}">
                    <button type="submit"><spring:message code="button.remove.favorites"/></button>
                </a>
            </c:otherwise>
        </c:choose>
    </div>

    <c:if test="${albums.size() > 0}">
        <h2><spring:message code="label.album"/></h2>
        <div class="carousel-container">
            <div class="carousel">
                <c:forEach var="album" items="${albums}" varStatus="status">
                    <c:url var="albumUrl" value="/album/${album.id}"/>
                    <div class="item">
                        <a href="${albumUrl}" class="album">
                            <div class="album-image-container">
                                <c:url var="albumImgURL" value="/images/${album.image.id}"/>
                                <img src="${albumImgURL}" alt="Album ${status.index + 1}">
                                <div class="album-rating">
                                    <fmt:formatNumber value="${album.avgRating}" maxFractionDigits="1" var="formattedRating"/>
                                    <span class="rating">${formattedRating}</span>
                                    <span class="star">&#9733;</span>
                                </div>
                            </div>
                            <p class="album-title"><c:out value="${album.title}"/></p>
                        </a>
                    </div>
                </c:forEach>
            </div>
        </div>
    </c:if>

    <c:if test="${songs.size() > 0}">
        <h2><spring:message code="label.song"/></h2>
        <ul class="song-list">
            <c:forEach var="song" items="${songs}" varStatus="status">
                <c:url var="songUrl" value="/song/${song.id}"/>
                <li>
                    <a href="${songUrl}" class="song-item">
                        <span class="song-number">${status.index + 1}</span>
                        <span class="song-title"><c:out value="${song.title}"/></span>
                        <span class="song-rating">
                          <fmt:formatNumber value="${song.avgRating}" maxFractionDigits="1" var="formattedRating"/>
                          <span class="rating">${formattedRating}</span>
                          <span class="star">&#9733;</span>
                        </span>
                    </a>
                </li>
            </c:forEach>
        </ul>
    </c:if>

    <c:if test="${reviews.size() > 0}">
    <h2><spring:message code="label.reviews"/></h2>
    <div class="cards-container">
        <c:forEach var="review" items="${reviews}">
            <jsp:include page="/WEB-INF/jsp/components/review_card.jsp">
                <jsp:param name="item_img_id" value="${review.artist.image.id}"/>
                <jsp:param name="item_name" value="${review.artist.name}"/>
                <jsp:param name="item_url" value="/artist/${review.artist.id}"/>
                <jsp:param name="item_type" value="Artist"/>
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
            </jsp:include>
        </c:forEach>
    </div>
    <c:url value="/artist/${artist.id}?pageNum=${pageNum + 1}" var="nextPage" />
    <c:url value="/artist/${artist.id}?pageNum=${pageNum - 1}" var="prevPage" />
        <c:if test="${showPrevious}"><a href="${prevPage}"><button>Previous page</button></a></c:if>
        <c:if test="${showNext}"><a href="${nextPage}"><button>Next page</button></a></c:if>
    </c:if>
</div>
</body>
</html>