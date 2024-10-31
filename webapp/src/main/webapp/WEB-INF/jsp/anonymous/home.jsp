<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Musicboxd</title>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp"/>
</head>
<body>
    <!-- Links -->
    <c:url var="home" value="/"/>
    <c:url var="music" value="/music"/>
    <c:url var="login" value="/user/login"/>
    <c:url var="register" value="/user/register"/>

    <!-- Icons -->
    <c:url var="reviewIcon" value="/static/assets/reviewIcon.png" />
    <c:url var="communityIcon" value="/static/assets/communityIcon.png" />
    <c:url var="discoverIcon" value="/static/assets/discoverIcon.png" />

    <!-- Content -->
    <div class="main-container">
        <header class="landing-header">
                <nav class="nav-bar">
                    <div class="logo"><spring:message code="webpage.name"/></div>
                    <div class="nav-links">
                        <a href="${home}" class="nav-link"><spring:message code="page.title.home"/></a>
                        <a href="${music}" class="nav-link"><spring:message code="page.title.discovery"/></a>
                        <a href="${login}" class="nav-link"><spring:message code="label.login"/></a>
                        <a href="${register}" class="nav-link"><spring:message code="label.register"/></a>
                    </div>
                </nav>
        </header>

        <main class="content-wrapper">
            <section class="hero-section">
                <h1><spring:message code="label.webpage.discovery"/></h1>
                <p><spring:message code="label.webpage.description"/></p>
                <a href="${register}" class="btn btn-primary"><spring:message code="label.get.started"/></a>
            </section>

            <section class="features-grid">
                <div class="feature-card">
                    <img src="${reviewIcon}" alt="Review Icon" class="feature-icon">
                    <h3><spring:message code="label.write.reviews"/></h3>
                    <p><spring:message code="label.write.reviews.description"/></p>
                </div>
                <div class="feature-card">
                    <img src="${communityIcon}" alt="Community Icon" class="feature-icon">
                    <h3><spring:message code="label.join.community"/></h3>
                    <p><spring:message code="label.join.community.description"/></p>
                </div>
                <div class="feature-card">
                    <img src="${discoverIcon}" alt="Discover Icon" class="feature-icon">
                    <h3><spring:message code="label.discover.music"/></h3>
                    <p><spring:message code="label.discover.music.description"/></p>
                </div>
            </section>

            <section class="reviews-section">
                <h2><spring:message code="label.popular.reviews" /></h2>
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
                            <jsp:param name="moderator" value="${false}"/>
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
            </section>
            <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
        </main>

    </div>
</body>
</html>
