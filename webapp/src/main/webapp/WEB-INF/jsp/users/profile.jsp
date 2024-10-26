<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>

    <spring:message var="pageTitle" code="page.userpage.title"/>
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
    <header class="artist-info">
        <jsp:include page="/WEB-INF/jsp/components/user_info.jsp">
            <jsp:param name="imgId" value="${user.imgId}" />
            <jsp:param name="username" value="${user.username}" />
            <jsp:param name="name" value="${user.name}" />
            <jsp:param name="bio" value="${user.bio}" />
            <jsp:param name="reviewAmount" value="${user.reviewAmount}" />
            <jsp:param name="followersAmount" value="${user.followersAmount}" />
            <jsp:param name="followingAmount" value="${user.followingAmount}" />
            <jsp:param name="id" value="${user.id}" />
        </jsp:include>
    </header>
    <c:url var="editProfileUrl" value="/user/edit"/>
    <a href="${editProfileUrl}"><button><spring:message code="label.edit.profile"/></button></a>

    <h2><spring:message code="label.favorite.artists"/></h2>
    <c:if test="${artists.size() == 0}">
        <div class="artist">
            <span class="add-favorites"><spring:message code="label.up.to.five.artist"/></span>
        </div>
    </c:if>
    <div class="carousel-container">
        <div class="carousel">
            <c:forEach var="artist" items="${artists}" varStatus="status">
                <c:url var="artistUrl" value="/artist/${artist.id}"/>
                <div class="item">
                    <a href="${artistUrl}" class="artist">
                        <div class="album-image-container">
                            <c:url var="artistImgURL" value="/images/${artist.imgId}"/>
                            <img src="${artistImgURL}" alt="Artist ${status.index + 1}">
                            <div class="album-rating">
                                <fmt:formatNumber value="${artist.avgRating}" maxFractionDigits="1" var="formattedRating"/>
                                <span class="rating">${formattedRating}</span>
                                <span class="star">&#9733;</span>
                            </div>
                        </div>
                        <p class="album-title"><c:out value="${artist.name}"/></p>
                    </a>
                </div>
            </c:forEach>
        </div>
    </div>

    <h2><spring:message code="label.favorite.albums"/></h2>
    <c:if test="${albums.size() == 0}">
        <div class="artist">
            <span class="add-favorites"><spring:message code="label.up.to.five.albums"/></span>
        </div>
    </c:if>
    <div class="carousel-container">
        <div class="carousel">
            <c:forEach var="album" items="${albums}" varStatus="status">
                <c:url var="albumUrl" value="/album/${album.id}"/>
                <div class="item">
                    <a href="${albumUrl}" class="album">
                        <div class="album-image-container">
                            <c:url var="albumImgURL" value="/images/${album.imgId}"/>
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

    <h2><spring:message code="label.favorite.songs"/></h2>
    <c:if test="${songs.size() == 0}">
        <div class="artist">
            <span class="add-favorites"><spring:message code="label.up.to.five.songs"/></span>
        </div>
    </c:if>
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

    <!-- Cards Container -->
    <c:if test="${reviews.size() > 0}">
    <h2><spring:message code="label.reviews" /></h2>
            <div class="cards-container">
                <c:forEach var="review" items="${reviews}">
                    <jsp:include page="/WEB-INF/jsp/components/review_card.jsp">
                        <jsp:param name="item_img_id" value="${review.itemImgId}"/>
                        <jsp:param name="item_name" value="${review.itemName}"/>
                        <jsp:param name="item_url" value="/${review.itemLink}"/>
                        <jsp:param name="item_type" value="${review.itemType}"/>
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
                        <jsp:param name="blocked" value="${review.isBlocked()}"/>
                        <jsp:param name="commentAmount" value="${review.commentAmount}"/>
                        <jsp:param name="timeAgo" value="${review.timeAgo}"/>
                    </jsp:include>
                </c:forEach>
            </div>
            <div class="pages">
                <c:url value="/user/profile?pageNum=${pageNum + 1}" var="nextPage" />
                <c:url value="/user/profile?pageNum=${pageNum -1}" var="prevPage" />
                <c:if test="${showPrevious}"><a href="${prevPage}"><button><spring:message code="button.previous.page" /></button></a></c:if>
                <c:if test="${showNext}"><a href="${nextPage}"><button><spring:message code="button.next.page" /></button></a></c:if>
            </div>
    </c:if>
<jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
</div>
</body>
</html>