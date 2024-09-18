<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>

    <spring:message var="pageTitle" text="Artist Page"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${pageTitle}"/>
    </jsp:include>

    <c:url var="css" value="/static/css/artist.css" />
    <link rel="stylesheet" href="${css}">

</head>
<body>
<div>
    <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
        <jsp:param name="loggedUserImgId" value="${loggedUser.imgId}"/>
    </jsp:include>
</div>
<div class="container">
    <header>
        <c:url var="artistImgURL" value="/images/${artist.imgId}"/>
        <img src="${artistImgURL}" alt="Artist Name" class="artist-image">
        <div class="artist-info">
            <p class="artist-type">Artist</p>
            <h1><c:out value="${artist.name}"/></h1>
            <p class="artist-bio"><c:out value="${artist.bio}"/></p>
            <c:url value="/artist/${artist.id}/reviews" var="new_artist_review_url" />
            <a href="${new_artist_review_url}">
                <button>Make a review</button>
            </a>
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
    </header>

    <h2>Albums</h2>
    <div class="carousel">
        <c:forEach var="album" items="${albums}" varStatus="status">
            <c:url var="albumUrl" value="/album/${album.id}"/>
            <a href="${albumUrl}">
                <div class="album">
                    <c:url var="albumImgURL" value="/images/${album.imgId}"/>
                    <img src="${albumImgURL}" alt="Album ${status.index + 1}">
                    <p><c:out value="${album.title}"/></p>
                </div>
            </a>
        </c:forEach>
    </div>

    <h2>Popular Songs</h2>
    <ul class="song-list">
        <c:forEach var="song" items="${songs}" varStatus="status">
            <c:url var="songUrl" value="/song/${song.id}"/>
            <a href="${songUrl}">
                <li>
                    <span class="song-number">${status.index + 1}</span>
                    <span class="song-title"><c:out value="${song.title}"/></span>
                </li>
            </a>
        </c:forEach>
    </ul>

    <!-- Cards Container -->
    <div class="cards-container">
        <c:forEach var="review" items="${reviews}">
            <jsp:include page="/WEB-INF/jsp/components/review_card.jsp">
                <jsp:param name="item_img_id" value="${review.artist.imgId}"/>
                <jsp:param name="item_name" value="${review.artist.name}"/>
                <jsp:param name="item_url" value="/artist/${review.artist.id}"/>
                <jsp:param name="artist_url" value="/artist/${review.artist.id}"/>
                <jsp:param name="item_type" value="Artist"/>
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