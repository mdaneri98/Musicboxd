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
        <jsp:include page="/WEB-INF/jsp/components/artist_info.jsp">
            <jsp:param name="id" value="${artist.id}" />
            <jsp:param name="imgId" value="${artist.imgId}" />
            <jsp:param name="bio" value="${artist.bio}" />
            <jsp:param name="name" value="${artist.name}" />
            <jsp:param name="isFavorite" value="${isFavorite}" />
        </jsp:include>
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
                <jsp:param name="likes" value="${review.likes}"/>
                <jsp:param name="user_id" value="${review.user.id}"/>
                <jsp:param name="review_id" value="${review.id}"/>
            </jsp:include>
        </c:forEach>
    </div>
    <c:url value="/artist/${artist.id}/${pageNum + 1}" var="nextPage" />
    <c:url value="/artist/${artist.id}/${pageNum -1}" var="prevPage" />
    <a href="${prevPage}" class="button review-button">Previous page</a>
    <a href="${nextPage}" class="button review-button">Next page</a>
</div>
</body>
</html>