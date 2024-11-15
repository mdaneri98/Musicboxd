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
</head>
<body>
    <div class="main-container">
        <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
            <jsp:param name="loggedUserImgId" value="${loggedUser.image.id}"/>
            <jsp:param name="moderator" value="${loggedUser.moderator}"/>
            <jsp:param name="unreadNotificationCount" value="${loggedUser.unreadNotificationCount}"/>
        </jsp:include>

        <main class="content-wrapper">
            <!-- User Info Header -->
            <div class="profile-header">
                <header>
                    <jsp:include page="/WEB-INF/jsp/components/user_info.jsp">
                        <jsp:param name="imgId" value="${user.image.id}" />
                        <jsp:param name="username" value="${user.username}" />
                        <jsp:param name="name" value="${user.name}" />
                        <jsp:param name="bio" value="${user.bio}" />
                        <jsp:param name="reviewAmount" value="${user.reviewAmount}" />
                        <jsp:param name="followersAmount" value="${user.followersAmount}" />
                        <jsp:param name="followingAmount" value="${user.followingAmount}" />
                        <jsp:param name="id" value="${user.id}" />
                        <jsp:param name="verified" value="${user.verified}" />
                        <jsp:param name="moderator" value="${user.moderator}" />
                    </jsp:include>
                </header>

                <!-- Edit Profile Button -->
                <c:url var="editProfileUrl" value="/user/edit"/>
                <a href="${editProfileUrl}" class="btn btn-primary">
                    <spring:message code="label.edit.profile"/>
                </a>
            </div>

            <div class="tabs">
                <span id="favoritesButton" class="tab ${reviewsActive ? '' : 'active'}">
                    <spring:message code="label.favorites"/>
                </span>
                <span id="reviewsButton" class="tab ${reviewsActive ? 'active' : ''}">
                    <spring:message code="label.reviews"/>
                </span>
            </div>

            <!-- Favorites Sections -->
            <section class="favorites-section">
                <!-- Favorite Artists -->
                <h2><spring:message code="label.favorite.artists"/></h2>
                <c:if test="${artists.size() == 0}">
                    <div class="empty-state">
                        <p class="add-favorites"><spring:message code="label.up.to.five.artist"/></p>
                    </div>
                </c:if>
                <div class="carousel-container">
                    <div class="carousel">
                        <c:forEach var="artist" items="${artists}" varStatus="status">
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
                </div>

                <!-- Favorite Albums -->
                <h2><spring:message code="label.favorite.albums"/></h2>
                <c:if test="${albums.size() == 0}">
                    <div class="empty-state">
                        <p class="add-favorites"><spring:message code="label.up.to.five.albums"/></p>
                    </div>
                </c:if>
                <div class="carousel-container">
                    <div class="carousel">
                        <c:forEach var="album" items="${albums}" varStatus="status">
                            <c:url var="albumUrl" value="/album/${album.id}"/>
                            <div class="music-item album-item">
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
                </div>

                <!-- Favorite Songs -->
                <h2><spring:message code="label.favorite.songs"/></h2>
                <c:if test="${songs.size() == 0}">
                    <div class="empty-state">
                        <p class="add-favorites"><spring:message code="label.up.to.five.songs"/></p>
                    </div>
                </c:if>
                <ul class="song-list">
                    <c:forEach var="song" items="${songs}" varStatus="status">
                        <c:url var="songUrl" value="/song/${song.id}"/>
                        <li>
                            <a href="${songUrl}" class="song-item">
                                <span class="song-number"><c:out value="${status.index + 1}"/></span>
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
            </section>

            <!-- Reviews Section -->
            <c:if test="${reviews.size() > 0}">
                <section class="reviews-section">
                    <div class="reviews-grid">
                        <c:forEach var="review" items="${reviews}">
                            <jsp:include page="/WEB-INF/jsp/components/review_card.jsp">
                                <jsp:param name="item_img_id" value="${review.itemImage.id}"/>
                                <jsp:param name="item_name" value="${review.itemName}"/>
                                <jsp:param name="item_url" value="/${review.itemLink}"/>
                                <jsp:param name="item_type" value="${review.itemType}"/>
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
                                <jsp:param name="blocked" value="${review.isBlocked()}"/>
                                <jsp:param name="commentAmount" value="${review.commentAmount}"/>
                                <jsp:param name="timeAgo" value="${review.timeAgo}"/>
                            </jsp:include>
                        </c:forEach>
                    </div>

                    <!-- Pagination -->
                    <div class="pagination">
                        <c:url value="/user/profile?page=reviews&pageNum=${pageNum + 1}" var="nextPage" />
                        <c:url value="/user/profile?page=reviews&pageNum=${pageNum - 1}" var="prevPage" />
                        <c:if test="${showPrevious}">
                            <a href="${prevPage}" class="btn btn-secondary">
                                <spring:message code="button.previous.page" />
                            </a>
                        </c:if>
                        <c:if test="${showNext}">
                            <a href="${nextPage}" class="btn btn-secondary">
                                <spring:message code="button.next.page" />
                            </a>
                        </c:if>
                    </div>
                </section>
            </c:if>

            <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
        </main>
    </div>
    <script>
        $(document).ready(function() {
            <c:if test="${!reviewsActive}">
                $(".reviews-section").hide();
            </c:if>
            <c:if test="${reviewsActive}">
                $(".favorites-section").hide();
            </c:if>

            $("#favoritesButton").click(function () {
                $(".favorites-section").show();
                $(".reviews-section").hide();
                $(this).addClass("active");
                $("#reviewsButton").removeClass("active");
            });

            $("#reviewsButton").click(function () {
                $(".reviews-section").show();
                $(".favorites-section").hide();
                $(this).addClass("active");
                $("#favoritesButton").removeClass("active");
            });
        });
    </script>
</body>
</html>