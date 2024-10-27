<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <spring:message var="pageTitle" code="page.review.title"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${pageTitle}"/>
    </jsp:include>

    <c:url var="css" value="/static/css/home.css" />
    <link rel="stylesheet" href="${css}">

    <c:url var="review_card" value="/static/css/review_card.css" />
    <link rel="stylesheet" href="${review_card}">

</head>
<body>
<div>
    <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
        <jsp:param name="loggedUserImgId" value="${loggedUser.image.id}"/>
        <jsp:param name="moderator" value="${loggedUser.moderator}"/>
    </jsp:include>
</div>
<div class="container">
    <div class="cards-container">
        <jsp:include page="/WEB-INF/jsp/components/review_card.jsp">
            <jsp:param name="item_img_id" value="${review.itemImgId}"/>
            <jsp:param name="item_name" value="${review.itemName}"/>
            <jsp:param name="item_url" value="/${review.itemLink}"/>
            <jsp:param name="item_type" value="${review.itemType}"/>
            <jsp:param name="title" value="${review.title}"/>
            <jsp:param name="rating" value="${review.rating}"/>
            <jsp:param name="review_content" value="${review.description}"/>
            <jsp:param name="user_name" value="@${review.user.username}"/>
            <jsp:param name="user_img_id" value="${review.user.imgId}"/>
            <jsp:param name="verified" value="${review.user.verified}"/>
            <jsp:param name="moderator" value="${loggedUser.moderator}"/>
            <jsp:param name="userModerator" value="${review.user.moderator}"/>
            <jsp:param name="likes" value="${review.likes}"/>
            <jsp:param name="user_id" value="${review.user.id}"/>
            <jsp:param name="review_id" value="${review.id}"/>
            <jsp:param name="isLiked" value="${review.liked}"/>
        </jsp:include>
    </div>
</div>
</body>
</html>
