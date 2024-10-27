<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
    <spring:message var="pageTitle" code="page.title.home"/>
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
</head>
<body>
<div>
    <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
        <jsp:param name="loggedUserImgId" value="${loggedUser.image.id}"/>
        <jsp:param name="moderator" value="${loggedUser.moderator}"/>
    </jsp:include>
</div>
<div class="container">
    <div>
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <strong><spring:message code="message.error"/>:</strong>
                <c:out value="${error}"/>
            </div>
        </c:if>

        <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <strong><spring:message code="message.success"/>:</strong>
                <c:out value="${success}"/>
            </div>
        </c:if>
    </div>

    <div class="toggle">
        <span id="forYouButton" class="tab-button active"><spring:message code="home.for.you"/></span>
        <span>/</span>
        <span id="followingButton" class="tab-button"><spring:message code="home.following"/></span>
    </div>
    <div id="forYouTab">
        <h2><spring:message code="label.popular.reviews" /></h2>
        <c:if test="${popularReviews.size() == 0}">
            <div class="page-empty">
                <h3><spring:message code="home.page.empty"/></h3>
                <h4><spring:message code="home.try.previous"/></h4>
            </div>
        </c:if>
        <div class="cards-container">
            <c:forEach var="review" items="${popularReviews}">
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
                </jsp:include>
            </c:forEach>
        </div>
    </div>

    <div id="followingTab">
        <h2><spring:message code="label.following.reviews"/></h2>
        <c:if test="${followingReviews.size() == 0}">
            <div class="page-empty">
                <h3><spring:message code="home.page.empty"/></h3>
                <h4><spring:message code="home.try.following"/></h4>
            </div>
        </c:if>
        <div class="cards-container">
            <c:forEach var="review" items="${followingReviews}">
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
                </jsp:include>
            </c:forEach>
        </div>
    </div>

    <div class="pages">
        <c:url value="/home?pageNum=${pageNum + 1}" var="nextPage" />
        <c:url value="/home?pageNum=${pageNum - 1}" var="prevPage" />
        <c:if test="${showPrevious}"><a href="${prevPage}"><button><spring:message code="button.previous.page"/></button></a></c:if>
        <c:if test="${showNext}"><a href="${nextPage}"><button><spring:message code="button.next.page"/></button></a></c:if>
    </div>
</div>

<script>
    $(document).ready(function() {
        // Ocultar el mensaje de error después de 5 segundos
        setTimeout(function() {
            $('#errorAlert').alert('close');
        }, 5000); // 5000ms = 5 segundos

        // Ocultar el mensaje de éxito después de 5 segundos
        setTimeout(function() {
            $('#successAlert').alert('close');
        }, 5000); // 5000ms = 5 segundos
    });
</script>

</body>
</html>