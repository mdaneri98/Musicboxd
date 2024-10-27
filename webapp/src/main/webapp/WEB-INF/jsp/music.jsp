<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <spring:message var="pageTitle" code="page.title.discovery"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${pageTitle}"/>
    </jsp:include>

    <c:url var="css" value="/static/css/home.css" />
    <link rel="stylesheet" href="${css}">

    <c:url var="review_card" value="/static/css/review_card.css" />
    <link rel="stylesheet" href="${review_card}">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
        $(document).ready(function() {
            $("#topRatedArtistTab").hide();
            $("#topRatedAlbumTab").hide();
            $("#topRatedSongTab").hide();

            $("#popularArtistButton").click(function() {
                $("#popularArtistTab").show();
                $("#topRatedArtistTab").hide();
                $(this).addClass("active");
                $("#topRatedArtistButton").removeClass("active");
            });

            $("#topRatedArtistButton").click(function() {
                $("#topRatedArtistTab").show();
                $("#popularArtistTab").hide();
                $(this).addClass("active");
                $("#popularArtistButton").removeClass("active");
            });

            $("#popularAlbumButton").click(function() {
                $("#popularAlbumTab").show();
                $("#topRatedAlbumTab").hide();
                $(this).addClass("active");
                $("#topRatedAlbumButton").removeClass("active");
            });

            $("#topRatedAlbumButton").click(function() {
                $("#topRatedAlbumTab").show();
                $("#popularAlbumTab").hide();
                $(this).addClass("active");
                $("#popularAlbumButton").removeClass("active");
            });

            $("#popularSongButton").click(function() {
                $("#popularSongTab").show();
                $("#topRatedSongTab").hide();
                $(this).addClass("active");
                $("#topRatedSongButton").removeClass("active");
            });

            $("#topRatedSongButton").click(function() {
                $("#topRatedSongTab").show();
                $("#popularSongTab").hide();
                $(this).addClass("active");
                $("#popularSongButton").removeClass("active");
            });
        });
    </script>
</head>
<body>
<div>
    <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
        <jsp:param name="loggedUserImgId" value="${loggedUser.image.id}"/>
        <jsp:param name="moderator" value="${loggedUser.moderator}"/>
    </jsp:include>
</div>
<div class="container">

    <div class="call-to-action-container">
        <h1><spring:message code="discovery" /></h1>
        <h4><spring:message code="discovery.description" /></h4>
    </div>

    <div class="toggle">
        <span id="popularArtistButton" class="tab-button active"><spring:message code="discovery.most-popular"/></span>
        <span>/</span>
        <span id="topRatedArtistButton" class="tab-button"><spring:message code="discovery.top-rated"/></span>
    </div>
    <div id="topRatedArtistTab">
        <c:if test="${topRatedArtists.size() > 0}">
            <h2><spring:message code="discovery.top-rated.artist"/></h2>
            <div class="carousel-container">
                <div class="carousel">
                    <c:forEach var="artist" items="${topRatedArtists}" varStatus="status">
                        <c:url var="artistUrl" value="/artist/${artist.id}"/>
                        <div class="item">
                            <a href="${artistUrl}" class="artist">
                                <div class="album-image-container">
                                    <c:url var="artistImgURL" value="/images/${artist.image.id}"/>
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
        </c:if>
    </div>

    <div id="popularArtistTab">
        <c:if test="${mostPopularArtists.size() > 0}">
            <h2><spring:message code="discovery.most-popular.artist"/></h2>
            <div class="carousel-container">
                <div class="carousel">
                    <c:forEach var="artist" items="${mostPopularArtists}" varStatus="status">
                        <c:url var="artistUrl" value="/artist/${artist.id}"/>
                        <div class="item">
                            <a href="${artistUrl}" class="artist">
                                <div class="album-image-container">
                                    <c:url var="artistImgURL" value="/images/${artist.image.id}"/>
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
        </c:if>
    </div>

    <div class="toggle">
        <span id="popularAlbumButton" class="tab-button active"><spring:message code="discovery.most-popular"/></span>
        <span>/</span>
        <span id="topRatedAlbumButton" class="tab-button"><spring:message code="discovery.top-rated"/></span>
    </div>
    <div id="topRatedAlbumTab">
        <c:if test="${topRatedAlbums.size() > 0}">
            <h2><spring:message code="discovery.top-rated.album"/></h2>
            <div class="carousel-container">
                <div class="carousel">
                    <c:forEach var="album" items="${topRatedAlbums}" varStatus="status">
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
    </div>

    <div id="popularAlbumTab">
        <c:if test="${mostPopularAlbums.size() > 0}">
            <h2><spring:message code="discovery.most-popular.album"/></h2>
            <div class="carousel-container">
                <div class="carousel">
                    <c:forEach var="album" items="${mostPopularAlbums}" varStatus="status">
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
    </div>

    <div class="toggle">
        <span id="popularSongButton" class="tab-button active"><spring:message code="discovery.most-popular"/></span>
        <span>/</span>
        <span id="topRatedSongButton" class="tab-button"><spring:message code="discovery.top-rated"/></span>
    </div>
    <div id="topRatedSongTab">
        <c:if test="${topRatedSongs.size() > 0}">
            <h2><spring:message code="discovery.top-rated.song"/></h2>
            <ul class="song-list">
                <c:forEach var="song" items="${topRatedSongs}" varStatus="status">
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
    </div>

    <div id="popularSongTab">
        <c:if test="${mostPopularSongs.size() > 0}">
            <h2><spring:message code="discovery.most-popular.song"/></h2>
            <ul class="song-list">
                <c:forEach var="song" items="${mostPopularSongs}" varStatus="status">
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
    </div>
<jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
</div>
