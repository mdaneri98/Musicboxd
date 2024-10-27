<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Musicboxd</title>
    <c:url var="logoUrl" value="/static/assets/logo.png"/>
    <link rel="icon" type="image/x-icon" href="${logoUrl}">

    <c:url var="css" value="/static/css/home.css" />
    <link rel="stylesheet" href="${css}">

    <c:url var="css2" value="/static/css/landing.css" />
    <link rel="stylesheet" href="${css2}">

    <c:url var="css3" value="/static/css/review_card.css" />
    <link rel="stylesheet" href="${css3}">

</head>
<body>
<c:url var="home" value="/"/>
<c:url var="music" value="/music"/>
<c:url var="login" value="/user/login"/>
<c:url var="register" value="/user/register"/>
<header>
    <div class="container">
        <nav>
            <div class="logo"><spring:message code="webpage.name"/></div>
            <div class="nav-links">
                <a href="${home}"><spring:message code="page.title.home"/></a>
                <a href="${music}"><spring:message code="page.title.discovery"/></a>
                <a href="${login}"><spring:message code="label.login"/></a>
                <a href="${register}"><spring:message code="label.register"/></a>
            </div>
        </nav>
    </div>
</header>

<main>
    <section class="hero">
        <div class="container">
            <h1><spring:message code="label.webpage.discovery"/></h1>
            <p><spring:message code="label.webpage.description"/></p>
            <a href="${register}" class="cta-button"><spring:message code="label.get.started"/></a>
        </div>
    </section>

    <section class="features">
        <div class="feature">
            <c:url var="reviewIcon" value="/static/assets/reviewIcon.png" />
            <img src="${reviewIcon}" alt="Review Icon">
            <h3><spring:message code="label.write.reviews"/></h3>
            <p><spring:message code="label.write.reviews.description"/></p>
        </div>
        <div class="feature">
            <c:url var="communityIcon" value="/static/assets/communityIcon.png" />
            <img src="${communityIcon}" alt="Community Icon">
            <h3><spring:message code="label.join.community"/></h3>
            <p><spring:message code="label.join.community.description"/></p>
        </div>
        <div class="feature">
            <c:url var="discoverIcon" value="/static/assets/discoverIcon.png" />
            <img src="${discoverIcon}" alt="Discover Icon">
            <h3><spring:message code="label.discover.music"/></h3>
            <p><spring:message code="label.discover.music.description"/></p>
        </div>
    </section>
</main>

<div class="max-width">
    <h2><spring:message code="label.popular.reviews" /></h2>
    <div class="cards-container">
        <c:forEach var="review" items="${popularReviews}">
            <jsp:include page="/WEB-INF/jsp/components/review_card.jsp">
                <jsp:param name="item_img_id" value="${review.itemImgId}"/>
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
            </jsp:include>
        </c:forEach>
    </div>
    <div class="pages">
        <c:url value="/home/${pageNum + 1}" var="nextPage" />
        <c:url value="/home/${pageNum -1}" var="prevPage" />
        <c:if test="${pageNum > 1}"><a href="${prevPage}"><button><spring:message code="button.previous.page" /></button></a></c:if>
        <c:if test="${popularReviews.size() == 10}"><a href="${nextPage}"><button><spring:message code="button.next.page" /></button></a></c:if>
    </div>
</div>

<footer>
    <div class="container">
        <p>&copy; 2024 Musicboxd. <spring:message code="label.rights.reserved"/></p>
    </div>
</footer>
</html>
