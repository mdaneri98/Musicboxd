<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <spring:message var="pageTitle" code="page.review.title"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${pageTitle}"/>
    </jsp:include>

    <c:url var="css" value="/static/css/comment.css" />
    <link rel="stylesheet" href="${css}">

    <c:url var="review_card" value="/static/css/review_card.css" />
    <link rel="stylesheet" href="${review_card}">

</head>
<body>
<div>
    <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
        <jsp:param name="loggedUserImgId" value="${loggedUser.imgId}"/>
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
            <jsp:param name="commentAmount" value="${review.commentAmount}"/>
        </jsp:include>
    </div>
    
    <div class="comments-section">
        <h3><spring:message code="comments.title"/></h3>

        <c:url value='/review/${review.id}/comment' var="postComment"/>
        <form:form modelAttribute="commentForm" method="POST" action="${postComment}">
            <form:textarea path="content" maxlength="500" class="comment-input"/>
            <form:errors path="content" cssClass="error" />
            <button type="submit" class="comment-submit"><spring:message code="comments.submit"/></button>
        </form:form>

        <div class="comments-list">
            <c:forEach var="comment" items="${comments}">
                <div class="comment-card">
                    <c:url var="profileUrl" value="/user/${comment.user.id}"/>
                    <div class="comment-header">
                        <a href="${profileUrl}">
                            <img src="<c:url value='/images/${comment.user.imgId}'/>" class="comment-user-img" alt="${comment.user.username}">
                            <span class="comment-username">${comment.user.username}</span>
                        </a>
                        <span class="comment-date"><c:out value="${comment.createdAt}"/></span>
                        <c:if test="${loggedUser.id == comment.user.id || loggedUser.moderator}">
                            <c:url value='/review/${review.id}/comment/${comment.id}/delete' var="deleteCommentUrl"/>
                            <a href="${deleteCommentUrl}" class="comment-delete"><spring:message code="comments.delete"/></a>
                        </c:if>
                    </div>
                    <p class="comment-content">${comment.content}</p>
                </div>
            </c:forEach>
        </div>
    </div>
</div>
</body>
</html>
