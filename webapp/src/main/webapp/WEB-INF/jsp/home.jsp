<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
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
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
        $(document).ready(function() {
            $("#followingTab").hide();

            $("#forYouButton").click(function() {
                $("#forYouTab").show();
                $("#followingTab").hide();
                $(this).addClass("active");
                $("#followingButton").removeClass("active");
            });

            $("#followingButton").click(function() {
                $("#followingTab").show();
                $("#forYouTab").hide();
                $(this).addClass("active");
                $("#forYouButton").removeClass("active");
            });
        });
    </script>
    <style>
        .tab-button { cursor: pointer; padding: 10px; }
        .active { background-color: #ddd; }
    </style>
</head>
<body>
<div>
    <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
        <jsp:param name="loggedUserImgId" value="${loggedUser.imgId}"/>
        <jsp:param name="moderator" value="${loggedUser.moderator}"/>
    </jsp:include>
</div>
<div class="container">
    <div>
        <span id="forYouButton" class="tab-button active">For You</span>
        <span id="followingButton" class="tab-button">Following</span>
    </div>

    <div id="forYouTab">
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
                    <jsp:param name="moderator" value="${loggedUser.moderator}"/>
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

    <div id="followingTab">
        <h2>Reviews from Users You Follow</h2>
        <div class="cards-container">
            <c:forEach var="review" items="${followingReviews}">
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
            </c:forEach>
        </div>
        <div class="pages">
        <c:url value="/home/${pageNum + 1}" var="nextPage" />
        <c:url value="/home/${pageNum -1}" var="prevPage" />
        <c:if test="${pageNum > 1}"><a href="${prevPage}"><button>Previous page</button></a></c:if>
        <c:if test="${followingReviews.size() == 10}"><a href="${nextPage}"><button>Next page</button></a></c:if>
        </div>
    </div>


</div>
</body>
</html>