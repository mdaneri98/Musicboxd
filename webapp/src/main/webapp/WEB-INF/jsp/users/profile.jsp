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

</head>
<body>
<div>
    <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
        <jsp:param name="loggedUserImgId" value="${loggedUser.imgId}"/>
    </jsp:include>
</div>
<div class="main-content container">
    <header>
        <c:url var="userImgURL" value="/images/${loggedUser.imgId}"/>
        <img src="${userImgURL}" alt="User Name" class="artist-image">
        <div class="artist-info">
            <p class="artist-type"></p>
            <h1>@<c:out value="${loggedUser.username}"/></h1>
            <h3><c:out value="${loggedUser.name}"/></h3>
            <p class="artist-bio"><c:out value="${loggedUser.bio}"/></p>
            <div class="user-stats">
      <span class="stat-item">
        <strong><c:out value="${loggedUser.reviewAmount}"/></strong> Posts
      </span>
                <span class="stat-item">
        <strong><c:out value="${loggedUser.followersAmount}"/></strong> Followers
      </span>
                <span class="stat-item">
        <strong><c:out value="${loggedUser.followingAmount}"/></strong> Following
      </span>
            </div>
            <c:url value="/user/edit" var="edit_profile_url" />
            <a href="${edit_profile_url}">
                <button>Edit Profile</button>
            </a>
        </div>
    </header>

    <h2>Favourite Albums</h2>
    <c:if test="${albums.size() == 0}">
        <div class="artist">
            <p>Add up to 5 favorite albums!</p>
        </div>
    </c:if>
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

    <h2>Favourite artists</h2>
    <c:if test="${artists.size() == 0}">
        <div class="artist">
            <p>Add up to 5 favorite artists!</p>
        </div>
    </c:if>
    <div class="carousel">
        <c:forEach var="artist" items="${artists}" varStatus="status">
            <c:url var="artistUrl" value="/artist/${artist.id}"/>
            <a href="${artistUrl}">
                <div class="artist">
                    <c:url var="artistImgURL" value="/images/${artist.imgId}"/>
                    <img src="${artistImgURL}" alt="Album ${status.index + 1}">
                    <p><c:out value="${artist.name}"/></p>
                </div>
            </a>
        </c:forEach>
    </div>

    <h2>Favourite Songs</h2>
    <c:if test="${songs.size() == 0}">
        <div class="artist">
            <p>Add up to 5 favorite songs!</p>
        </div>
    </c:if>
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
                <jsp:param name="title" value="${review.title}"/>
                <jsp:param name="description" value="${review.description}"/>
                <jsp:param name="userId" value="${review.userId}"/>
                <jsp:param name="imgId" value="${artist.imgId}"/>
            </jsp:include>
        </c:forEach>
    </div>
</div>
</body>
</html>