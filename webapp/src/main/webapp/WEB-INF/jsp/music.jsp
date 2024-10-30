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
</head>
<body>
    <div class="main-container">
        <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
            <jsp:param name="loggedUserImgId" value="${loggedUser.image.id}"/>
            <jsp:param name="moderator" value="${loggedUser.moderator}"/>
        </jsp:include>
        
        <main class="content-wrapper">
            <div class="discovery-header">
                <div class="call-to-action-container">
                    <h1><spring:message code="discovery" /></h1>
                    <h4><spring:message code="discovery.description" /></h4>
                </div>
            </div>

            <!-- Artists Section -->
            <section class="discovery-section">
                <div class="section-header">
                    <div class="tabs">
                        <span id="popularArtistButton" class="tab active">
                            <spring:message code="discovery.most-popular"/>
                        </span>
                        <span class="tab-separator">/</span>
                        <span id="topRatedArtistButton" class="tab">
                            <spring:message code="discovery.top-rated"/>
                        </span>
                    </div>
                </div>

                <div id="topRatedArtistTab" class="tab-content">
                    <c:if test="${topRatedArtists.size() > 0}">
                        <h2><spring:message code="discovery.top-rated.artist"/></h2>
                        <div class="carousel-container">
                            <div class="carousel">
                                <c:forEach var="artist" items="${topRatedArtists}" varStatus="status">
                                    <c:url var="artistUrl" value="/artist/${artist.id}"/>
                                    <div class="music-item artist-item">
                                        <a href="${artistUrl}" class="music-item-link">
                                            <div class="music-item-image-container">
                                                <c:url var="artistImgURL" value="/images/${artist.image.id}"/>
                                                <img src="${artistImgURL}" alt="${artist.name}" class="music-item-image">
                                                <div class="rating-badge">
                                                    <fmt:formatNumber value="${artist.avgRating}" maxFractionDigits="1" var="formattedRating"/>
                                                    <span class="rating">${formattedRating}</span>
                                                    <span class="star">&#9733;</span>
                                                </div>
                                            </div>
                                            <p class="music-item-title"><c:out value="${artist.name}"/></p>
                                        </a>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </c:if>
                </div>

                <div id="popularArtistTab" class="tab-content">
                    <c:if test="${mostPopularArtists.size() > 0}">
                        <h2><spring:message code="discovery.most-popular.artist"/></h2>
                        <div class="carousel-container">
                            <div class="carousel">
                                <c:forEach var="artist" items="${mostPopularArtists}" varStatus="status">
                                    <c:url var="artistUrl" value="/artist/${artist.id}"/>
                                    <div class="music-item artist-item">
                                        <a href="${artistUrl}" class="music-item-link">
                                            <div class="music-item-image-container">
                                                <c:url var="artistImgURL" value="/images/${artist.image.id}"/>
                                                <img src="${artistImgURL}" alt="${artist.name}" class="music-item-image">
                                                <div class="rating-badge">
                                                    <fmt:formatNumber value="${artist.avgRating}" maxFractionDigits="1" var="formattedRating"/>
                                                    <span class="rating">${formattedRating}</span>
                                                    <span class="star">&#9733;</span>
                                                </div>
                                            </div>
                                            <p class="music-item-title"><c:out value="${artist.name}"/></p>
                                        </a>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </c:if>
                </div>
            </section>

            <!-- Albums Section -->
            <section class="discovery-section">
                <div class="section-header">
                    <div class="tabs">
                        <span id="popularAlbumButton" class="tab active">
                            <spring:message code="discovery.most-popular"/>
                        </span>
                        <span class="tab-separator">/</span>
                        <span id="topRatedAlbumButton" class="tab">
                            <spring:message code="discovery.top-rated"/>
                        </span>
                    </div>
                </div>

                <div id="topRatedAlbumTab" class="tab-content">
                    <c:if test="${topRatedAlbums.size() > 0}">
                        <h2><spring:message code="discovery.top-rated.album"/></h2>
                        <div class="carousel-container">
                            <div class="carousel">
                                <c:forEach var="album" items="${topRatedAlbums}" varStatus="status">
                                    <c:url var="albumUrl" value="/album/${album.id}"/>
                                    <div class="music-item album-item">
                                        <a href="${albumUrl}" class="music-item-link">
                                            <div class="music-item-image-container">
                                                <c:url var="albumImgURL" value="/images/${album.image.id}"/>
                                                <img src="${albumImgURL}" alt="${album.title}" class="music-item-image">
                                                <div class="rating-badge">
                                                    <fmt:formatNumber value="${album.avgRating}" maxFractionDigits="1" var="formattedRating"/>
                                                    <span class="rating">${formattedRating}</span>
                                                    <span class="star">&#9733;</span>
                                                </div>
                                            </div>
                                            <p class="music-item-title"><c:out value="${album.title}"/></p>
                                        </a>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </c:if>
                </div>

                <div id="popularAlbumTab" class="tab-content">
                    <c:if test="${mostPopularAlbums.size() > 0}">
                        <h2><spring:message code="discovery.most-popular.album"/></h2>
                        <div class="carousel-container">
                            <div class="carousel">
                                <c:forEach var="album" items="${mostPopularAlbums}" varStatus="status">
                                    <c:url var="albumUrl" value="/album/${album.id}"/>
                                    <div class="music-item album-item">
                                        <a href="${albumUrl}" class="music-item-link">
                                            <div class="music-item-image-container">
                                                <c:url var="albumImgURL" value="/images/${album.image.id}"/>
                                                <img src="${albumImgURL}" alt="${album.title}" class="music-item-image">
                                                <div class="rating-badge">
                                                    <fmt:formatNumber value="${album.avgRating}" maxFractionDigits="1" var="formattedRating"/>
                                                    <span class="rating">${formattedRating}</span>
                                                    <span class="star">&#9733;</span>
                                                </div>
                                            </div>
                                            <p class="music-item-title"><c:out value="${album.title}"/></p>
                                        </a>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </c:if>
                </div>
            </section>

            <!-- Songs Section -->
            <section class="discovery-section">
                <div class="section-header">
                    <div class="tabs">
                        <span id="popularSongButton" class="tab active">
                            <spring:message code="discovery.most-popular"/>
                        </span>
                        <span class="tab-separator">/</span>
                        <span id="topRatedSongButton" class="tab">
                            <spring:message code="discovery.top-rated"/>
                        </span>
                    </div>
                </div>

                <div id="topRatedSongTab" class="tab-content">
                    <c:if test="${topRatedSongs.size() > 0}">
                        <h2><spring:message code="discovery.top-rated.song"/></h2>
                        <ul class="song-list">
                            <c:forEach var="song" items="${topRatedSongs}" varStatus="status">
                                <c:url var="songUrl" value="/song/${song.id}"/>
                                <li>
                                    <a href="${songUrl}" class="song-item">
                                        <span class="song-number">${status.index + 1}</span>
                                        <span class="song-title"><c:out value="${song.title}"/></span>
                                        <div class="rating-badge">
                                            <fmt:formatNumber value="${song.avgRating}" maxFractionDigits="1" var="formattedRating"/>
                                            <span class="rating">${formattedRating}</span>
                                            <span class="star">&#9733;</span>
                                        </div>
                                    </a>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:if>
                </div>

                <div id="popularSongTab" class="tab-content">
                    <c:if test="${mostPopularSongs.size() > 0}">
                        <h2><spring:message code="discovery.most-popular.song"/></h2>
                        <ul class="song-list">
                            <c:forEach var="song" items="${mostPopularSongs}" varStatus="status">
                                <c:url var="songUrl" value="/song/${song.id}"/>
                                <li>
                                    <a href="${songUrl}" class="song-item">
                                        <span class="song-number">${status.index + 1}</span>
                                        <span class="song-title"><c:out value="${song.title}"/></span>
                                        <div class="rating-badge">
                                            <fmt:formatNumber value="${song.avgRating}" maxFractionDigits="1" var="formattedRating"/>
                                            <span class="rating">${formattedRating}</span>
                                            <span class="star">&#9733;</span>
                                        </div>
                                    </a>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:if>
                </div>
            </section>

            <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
        </main>
    </div>

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
</body>
</html>
