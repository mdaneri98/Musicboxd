<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
    <spring:message var="pageTitle" code="page.title.home"/>
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
            <!-- Alertas -->
            <c:if test="${not empty error || not empty success}">
                <div class="alerts-container">
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger" role="alert" id="errorAlert">
                            <strong><spring:message code="message.error"/>:</strong>
                            <c:out value="${error}"/>
                        </div>
                    </c:if>
                    <c:if test="${not empty success}">
                        <div class="alert alert-success" role="alert" id="successAlert">
                            <strong><spring:message code="message.success"/>:</strong>
                            <c:out value="${success}"/>
                        </div>
                    </c:if>
                </div>
            </c:if>

            <!-- Tabs de navegación -->
            <div class="section-header-home">
                <div class="tabs">
                    <c:if test="${forYouActive}">
                        <span class="tab active"><spring:message code="home.for.you"/></span>
                        <c:url var="followingUrl" value="/home?pageNum=1&page=following"/>
                        <a href="${followingUrl}">
                            <span class="tab"><spring:message code="home.following"/></span>
                        </a>
                    </c:if>
                    <c:if test="${followingActive}">
                        <c:url var="forYouUrl" value="/home?pageNum=1&page=forYou"/>
                        <a href="${forYouUrl}" >
                            <span class="tab"><spring:message code="home.for.you"/></span>
                        </a>
                        <span class="tab active"><spring:message code="home.following"/></span>
                    </c:if>
                </div>
            </div>

            <!-- Contenido principal -->
            <section class="reviews-section">
                <c:if test="${reviews.size() == 0}">
                    <div class="empty-state">
                        <h3><spring:message code="home.page.empty"/></h3>
                        <h4><spring:message code="home.try.previous"/></h4>
                    </div>
                </c:if>

                <div class="reviews-grid">
                    <c:forEach var="review" items="${reviews}">
                        <jsp:include page="/WEB-INF/jsp/components/review_card.jsp">
                            <jsp:param name="item_name" value="${review.itemName}"/>
                            <jsp:param name="item_img_id" value="${review.itemImage.id}"/>
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
                            <jsp:param name="commentAmount" value="${review.commentAmount}"/>
                            <jsp:param name="timeAgo" value="${review.timeAgo}"/>
                        </jsp:include>
                    </c:forEach>
                </div>

                <!-- Paginación -->
                <div class="pagination">
                    <c:url value="/home?pageNum=${pageNum + 1}&page=${forYouActive ? 'forYou' : 'following'}" var="nextPage" />
                    <c:url value="/home?pageNum=${pageNum - 1}&page=${forYouActive ? 'forYou' : 'following'}" var="prevPage" />
                    <c:if test="${showPrevious}">
                        <a href="${prevPage}" class="btn btn-secondary"><spring:message code="button.previous.page"/></a>
                    </c:if>
                    <c:if test="${showNext}">
                        <a href="${nextPage}" class="btn btn-secondary"><spring:message code="button.next.page"/></a>
                    </c:if>
                </div>
            </section>

            <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
        </main>
    </div>

    <script>
        $(document).ready(function() {
            setTimeout(function() {
                $('#errorAlert').alert('close');
                $('#successAlert').alert('close');
            }, 5000);
        });
    </script>
</body>
</html>