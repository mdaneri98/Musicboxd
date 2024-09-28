<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>

    <spring:message var="pageTitle" text="Artist Page"/>
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
        <c:url var="artistImgURL" value="/images/${artist.imgId}"/>
        <img src="${artistImgURL}" alt="Artist Name" class="primary-image">
        <div class="data-container">
            <p class="type">Artist</p>
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
                    <button type="submit">Add to favorites</button>
                </a>
            </c:when>
            <c:otherwise>
                <a href="${remove_favorite_url}">
                    <button type="submit">Remove from favorites</button>
                </a>
            </c:otherwise>
        </c:choose>
    </div>

    <c:if test="${albums.size() > 0}">
    <h2>Albums</h2>
    <div class="carousel-container">
        <div class="carousel">
            <c:forEach var="album" items="${albums}" varStatus="status">
                <c:url var="albumUrl" value="/album/${album.id}"/>
                <div class="item">
                    <a href="${albumUrl}" class="album">
                        <c:url var="albumImgURL" value="/images/${album.imgId}"/>
                        <img src="${albumImgURL}" alt="Album ${status.index + 1}">
                        <p><c:out value="${album.title}"/></p>
                    </a>
                </div>
            </c:forEach>
        </div>
    </div>
    </c:if>

    <c:if test="${songs.size() > 0}">
    <h2>Popular Songs</h2>
    <ul class="song-list">
        <c:forEach var="song" items="${songs}" varStatus="status">
            <c:url var="songUrl" value="/song/${song.id}"/>
            <a href="${songUrl}">
                <li>
                    <span class="song-number">${status.index + 1}     </span>
                    <span class="song-title"><c:out value="${song.title}"/></span>
                </li>
            </a>
        </c:forEach>
    </ul>
    </c:if>

    <c:if test="${reviews.size() > 0}">
    <h2>Reviews</h2>
    <div class="cards-container">
        <c:forEach var="review" items="${reviews}">
            <jsp:include page="/WEB-INF/jsp/components/review_card.jsp">
                <jsp:param name="item_img_id" value="${review.artist.imgId}"/>
                <jsp:param name="item_name" value="${review.artist.name}"/>
                <jsp:param name="item_url" value="/artist/${review.artist.id}"/>
                <jsp:param name="item_type" value="Artist"/>
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
    <c:url value="/artist/${artist.id}/${pageNum + 1}" var="nextPage" />
    <c:url value="/artist/${artist.id}/${pageNum -1}" var="prevPage" />
        <c:if test="${pageNum > 1}"><a href="${prevPage}"><button>Previous page</button></a></c:if>
        <c:if test="${reviews.size() == 5}"><a href="${nextPage}"><button>Next page</button></a></c:if>
    </c:if>
</div>
</body>
</html>