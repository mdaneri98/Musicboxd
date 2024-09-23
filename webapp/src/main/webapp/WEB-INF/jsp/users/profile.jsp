<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>

    <spring:message var="pageTitle" text="User Page"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${pageTitle}"/>
    </jsp:include>

    <c:url var="css" value="/static/css/artist.css" />
    <link rel="stylesheet" href="${css}">

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
    <header class="artist-info">
        <jsp:include page="/WEB-INF/jsp/components/user_info.jsp">
            <jsp:param name="imgId" value="${loggedUser.imgId}" />
            <jsp:param name="username" value="${loggedUser.username}" />
            <jsp:param name="name" value="${loggedUser.name}" />
            <jsp:param name="bio" value="${loggedUser.bio}" />
            <jsp:param name="reviewAmount" value="${loggedUser.reviewAmount}" />
            <jsp:param name="followersAmount" value="${loggedUser.followersAmount}" />
            <jsp:param name="followingAmount" value="${loggedUser.followingAmount}" />
            <jsp:param name="id" value="${loggedUser.id}" />
        </jsp:include>
    </header>

    <h2>Favorite Albums</h2>
    <c:if test="${albums.size() == 0}">
        <div class="artist">
            <p>Add up to 5 favorite albums!</p>
        </div>
    </c:if>
    <div class="medium-box-container">
        <c:forEach var="album" items="${albums}" varStatus="status">
            <c:url var="albumUrl" value="/album/${album.id}"/>
            <div class="medium-box-container-item">
                <a href="${albumUrl}">
                    <c:url var="albumImgURL" value="/images/${album.imgId}"/>
                    <img src="${albumImgURL}" alt="Album ${status.index + 1}">
                    <p><c:out value="${album.title}"/></p>
                </a>
            </div>
        </c:forEach>
    </div>

    <h2>Favorite artists</h2>
    <c:if test="${artists.size() == 0}">
        <div class="artist">
            <p>Add up to 5 favorite artists!</p>
        </div>
    </c:if>
    <div class="medium-box-container">
        <c:forEach var="artist" items="${artists}" varStatus="status">
            <c:url var="artistUrl" value="/artist/${artist.id}"/>
            <div class="medium-box-container-item">
                <a href="${artistUrl}">
                    <c:url var="artistImgURL" value="/images/${artist.imgId}"/>
                    <img src="${artistImgURL}" alt="Album ${status.index + 1}">
                    <p><c:out value="${artist.name}"/></p>
                </a>
            </div>
        </c:forEach>
    </div>

    <h2>Favorite Songs</h2>
    <c:if test="${songs.size() == 0}">
        <div class="artist">
            <p>Add up to 5 favorite songs!</p>
        </div>
    </c:if>
    <ul class="song-list">
        <c:forEach var="song" items="${songs}" varStatus="status">
            <c:url var="songUrl" value="/song/${song.id}"/>
            <li>
                <a href="${songUrl}">
                    <span class="song-number">${status.index + 1}</span>
                    <span class="song-title"><c:out value="${song.title}"/></span>
                </a>
            </li>
        </c:forEach>
    </ul>

    <!-- Cards Container -->
    <h2>Reviews</h2>
    <div class="h-reviews-container">
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
                <jsp:param name="likes" value="${review.likes}"/>
                <jsp:param name="user_id" value="${review.user.id}"/>
                <jsp:param name="review_id" value="${review.id}"/>
                <jsp:param name="isLiked" value="${review.liked}"/>
            </jsp:include>
        </c:forEach>
    </div>
    <c:url value="/user/profile/${pageNum + 1}" var="nextPage" />
    <c:url value="/user/profile/${pageNum -1}" var="prevPage" />
    <a href="${prevPage}" class="button review-button">Previous page</a>
    <a href="${nextPage}" class="button review-button">Next page</a>

</div>
</body>
</html>