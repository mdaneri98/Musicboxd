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

    <c:if test="${forYouActive}">
        <div class="toggle">
            <span class="tab-button active"><spring:message code="home.for.you"/></span>
        <span>/</span>
        <c:url var="followingUrl" value="/home?pageNum=1&page=following"/>
        <a href="${followingUrl}"><span class="tab-button"><spring:message code="home.following"/></span></a>
        </div>
    </c:if>
    <c:if test="${followingActive}">
        <div class="toggle">
            <c:url var="forYouUrl" value="/home?pageNum=1&page=forYou"/>
            <a href="${forYouUrl}"><span class="tab-button"><spring:message code="home.for.you"/></span></a>
        <span>/</span>
            <span class="tab-button active"><spring:message code="home.following"/></span>
        </div>
    </c:if>
    <div id="forYouTab">
        <h2><spring:message code="label.popular.reviews" /></h2>
        <c:if test="${reviews.size() == 0}">
            <div class="page-empty">
                <h3><spring:message code="home.page.empty"/></h3>
                <h4><spring:message code="home.try.previous"/></h4>
            </div>
        </c:if>
        <div class="cards-container">
            <c:forEach var="review" items="${reviews}">
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
                    <jsp:param name="commentAmount" value="${review.commentAmount}"/>
                    <jsp:param name="timeAgo" value="${review.timeAgo}"/>
                </jsp:include>
            </c:forEach>
        </div>
    </div>

    <div class="pages">
        <c:url value="/home?pageNum=${pageNum + 1}&page=${forYouActive ? 'forYou' : 'following'}" var="nextPage" />
        <c:url value="/home?pageNum=${pageNum - 1}&page=${forYouActive ? 'forYou' : 'following'}" var="prevPage" />
        <c:if test="${showPrevious}"><a href="${prevPage}"><button><spring:message code="button.previous.page"/></button></a></c:if>
        <c:if test="${showNext}"><a href="${nextPage}"><button><spring:message code="button.next.page"/></button></a></c:if>
    </div>
<jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
</div>
<script>
    $(document).ready(function() {
        setTimeout(function() {
            $('#errorAlert').alert('close');
        }, 5000);

        setTimeout(function() {
            $('#successAlert').alert('close');
        }, 5000);
    });
</script>

</body>
</html>