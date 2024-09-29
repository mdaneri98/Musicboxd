<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <spring:message var="pageTitle" text="Home"/>
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
    <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp" />
</div>
<div class="v-container">
    <div class="call-to-action-container">
        <h1>Musicboxd</h1>
        <h6>Descubre Nueva Música</h6>
        <p>Únete a una comunidad apasionada por la música y comparte tus opiniones sobre artistas, álbumes y canciones.</p>
    </div>
    <div>

    </div>
    <div class="max-width">
        <c:if test="${popularAlbums.size() > 0}">
            <h2>Popular albums</h2>
            <div class="carousel-container">
                <div class="carousel">
                    <c:forEach var="album" items="${popularAlbums}" varStatus="status">
                        <c:url var="albumUrl" value="/album/${album.id}"/>
                        <div class="item">
                            <a href="${albumUrl}" class="album">
                                <c:url var="albumImgURL" value="/images/${album.imgId}"/>
                                <img src="${albumImgURL}" alt="Album ${status.index + 1}">
                                <p><c:out value="${album.title}"/></p>
                            </a>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:if>
    </div>
    <div class="max-width">
        <h2>Popular Reviews</h2>
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
                    <jsp:param name="user_img_id" value="${review.user.imgId}"/>
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
            <c:if test="${pageNum > 1}"><a href="${prevPage}"><button>Previous page</button></a></c:if>
            <c:if test="${popularReviews.size() == 10}"><a href="${nextPage}"><button>Next page</button></a></c:if>
        </div>
    </div>

</div>
</body>
</html>
