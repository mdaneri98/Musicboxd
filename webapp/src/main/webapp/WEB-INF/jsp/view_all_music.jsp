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
            <jsp:param name="unreadNotificationCount" value="${loggedUser.unreadNotificationCount}"/>
        </jsp:include>
        
        <main class="content-wrapper">
            <!-- Tabs Navigation -->
            <div class="view-all-header">
                <div class="tabs-container">
                    <div class="tabs">
                        <c:url var="artistsUrl" value="/music/view-all?page=artists&filter=${filter}"/>
                        <c:url var="albumsUrl" value="/music/view-all?page=albums&filter=${filter}"/>
                        <c:url var="songsUrl" value="/music/view-all?page=songs&filter=${filter}"/>
                        <a href="${artistsUrl}" class="tab ${artistsActive ? 'active' : ''}">
                            <spring:message code="label.artists"/>
                        </a>
                        <a href="${albumsUrl}" class="tab ${albumsActive ? 'active' : ''}">
                            <spring:message code="label.albums"/>
                        </a>
                        <a href="${songsUrl}" class="tab ${songsActive ? 'active' : ''}">
                            <spring:message code="label.songs"/>
                        </a>
                    </div>
                </div>

                <!-- Filters -->
                <div class="filters-container">
                    <form action="" method="GET" class="filters-form">
                        <input type="hidden" name="page" value="${param.page}"/>
                        
                        <div class="filter-group">
                            <label for="sort" class="filter-label">
                                <spring:message code="filter.sort.by"/>
                            </label>
                            <select name="filter" id="sort" class="filter-select">
                                <option value="POPULAR" ${param.filter == 'POPULAR' ? 'selected' : ''}>
                                    <spring:message code="discovery.most-popular"/>
                                </option>
                                <option value="RATING" ${param.filter == 'RATING' ? 'selected' : ''}>
                                    <spring:message code="discovery.top-rated"/>
                                </option>
                                <option value="RECENT" ${param.filter == 'RECENT' ? 'selected' : ''}>
                                    <spring:message code="discovery.recently.added"/>
                                </option>
                                <option value="FIRST" ${param.filter == 'FIRST' ? 'selected' : ''}>
                                    <spring:message code="discovery.first.added"/>
                                </option>
                                <c:if test="${param.page == 'albums'}">
                                    <option value="NEWEST" ${param.filter == 'NEWEST' ? 'selected' : ''}>
                                    <spring:message code="discovery.newest"/>
                                </option>
                                    <option value="OLDEST" ${param.filter == 'OLDEST' ? 'selected' : ''}>
                                        <spring:message code="discovery.oldest"/>
                                    </option>
                                </c:if>
                            </select>
                        </div>

                        <button type="submit" class="btn btn-primary">
                            <spring:message code="filter.apply"/>
                        </button>
                    </form>
                </div>
            </div>

            <!-- Content Grid -->
            <div class="view-all-content">
                <c:choose>
                    <c:when test="${artistsActive}">
                        <div class="music-grid">
                            <c:forEach var="artist" items="${artists}">
                                <c:url var="artistUrl" value="/artist/${artist.id}"/>
                                <div class="music-item artist-item">
                                    <a href="${artistUrl}" class="music-item-link">
                                        <div class="music-item-image-container">
                                            <c:url var="artistImgURL" value="/images/${artist.image.id}"/>
                                            <img src="${artistImgURL}" alt="<c:out value="${artist.name}" />" class="music-item-image">
                                            <div class="rating-badge">
                                                <fmt:formatNumber value="${artist.avgRating}" maxFractionDigits="1" var="formattedRating"/>
                                                <span class="rating"><c:out value="${formattedRating}"/></span>
                                                <span class="star">&#9733;</span>
                                            </div>
                                        </div>
                                        <p class="music-item-title"><c:out value="${artist.name}"/></p>
                                    </a>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    
                    <c:when test="${albumsActive}">
                        <div class="music-grid">
                            <c:forEach var="album" items="${albums}">
                                <c:url var="albumUrl" value="/album/${album.id}"/>
                                <div class="music-item">
                                    <a href="${albumUrl}" class="music-item-link">
                                        <div class="music-item-image-container">
                                            <c:url var="albumImgURL" value="/images/${album.image.id}"/>
                                            <img src="${albumImgURL}" alt="<c:out value="${album.title}" />" class="music-item-image">
                                            <div class="rating-badge">
                                                <fmt:formatNumber value="${album.avgRating}" maxFractionDigits="1" var="formattedRating"/>
                                                <span class="rating"><c:out value="${formattedRating}"/></span>
                                                <span class="star">&#9733;</span>
                                            </div>
                                        </div>
                                        <p class="music-item-title"><c:out value="${album.title}"/></p>
                                    </a>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    
                    <c:when test="${songsActive}">
                        <ul class="song-list">
                            <c:forEach var="song" items="${songs}" varStatus="status">
                                <c:url var="songUrl" value="/song/${song.id}"/>
                                <li>
                                    <a href="${songUrl}" class="song-item">
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
                    </c:when>
                </c:choose>

                <!-- Pagination -->
                <div class="pagination">
                    <c:if test="${showPrevious}">
                        <a href="?page=${param.page}&pageNum=${currentPage - 1}&filter=${filter}" class="btn btn-secondary">
                            <spring:message code="button.previous.page"/>
                        </a>
                    </c:if>
                    
                    <c:if test="${showNext}">
                        <a href="?page=${param.page}&pageNum=${currentPage + 1}&filter=${filter}" class="btn btn-secondary">
                            <spring:message code="button.next.page"/>
                        </a>
                    </c:if>
                </div>
            </div>
            
            <div class="footer-placeholder">
                <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
            </div>
        </main>
    </div>
</body>
</html> 