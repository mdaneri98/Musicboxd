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
</head>
<body>
    <div class="main-container">
        <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
            <jsp:param name="loggedUserImgId" value="${loggedUser.image.id}"/>
            <jsp:param name="moderator" value="${loggedUser.moderator}"/>
        </jsp:include>

        <main class="content-wrapper">
            <!-- Review Card -->
            <div class="review-detail">
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
                    <jsp:param name="commentAmount" value="${review.commentAmount}"/>
                    <jsp:param name="timeAgo" value="${review.timeAgo}"/>
                </jsp:include>
            </div>
            
            <!-- Comments Section -->
            <section class="comments-section">
                <h3><spring:message code="comments.title"/></h3>

                <!-- Comment Form -->
                <c:url value='/review/${review.id}/comment' var="postComment"/>
                <form:form modelAttribute="commentForm" method="POST" action="${postComment}" class="comment-form">
                    <form:textarea path="content" maxlength="500" class="form-control comment-input" 
                                 placeholder="Write a comment..."/>
                    <form:errors path="content" cssClass="form-error" />
                    <button type="submit" class="btn btn-primary">
                        <spring:message code="comments.submit"/>
                    </button>
                </form:form>

                <!-- Comments List -->
                <div class="comments-list">
                    <c:forEach var="comment" items="${comments}">
                        <div class="comment-card">
                            <div class="comment-header">
                                <c:url var="profileUrl" value="/user/${comment.user.id}"/>
                                <a href="${profileUrl}" class="comment-user">
                                    <c:url var="userImgUrl" value="/images/${comment.user.image.id}"/>
                                    <img src="${userImgUrl}" alt="${comment.user.username}" class="comment-user-img">
                                    <span class="comment-username">${comment.user.username}</span>
                                </a>
                                <span class="comment-date"><c:out value="${comment.timeAgo}"/></span>
                                
                                <c:if test="${loggedUser.id == comment.user.id || loggedUser.moderator}">
                                    <c:url value='/review/${review.id}/comment/${comment.id}/delete' var="deleteCommentUrl"/>
                                    <button type="button" onclick="deleteComment(${comment.id})" class="btn btn-danger btn-sm">
                                        <spring:message code="comments.delete"/>
                                    </button>
                                </c:if>
                            </div>
                            <p class="comment-content">${comment.content}</p>
                        </div>
                    </c:forEach>
                </div>
            </section>

            <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
        </main>
    </div>

    <!-- Confirmation Modal -->
    <spring:message var="confirmation_text" code="confirmation.window.comment.message"/>
    <jsp:include page="/WEB-INF/jsp/components/confirmation-window.jsp">
        <jsp:param name="message" value="${confirmation_text}"/>
        <jsp:param name="id" value="Comment"/>
    </jsp:include>

    <script>
        function deleteComment(commentId) {
            var overlay = document.getElementById("modalOverlayComment");
            var modal = document.getElementById("confirmationModalComment");
            var yesButton = document.getElementById("modalYesComment");
            var noButton = document.getElementById("modalNoComment");

            overlay.style.display = "block";
            modal.style.display = "block";

            yesButton.onclick = function () {
                window.location.href = "${deleteCommentUrl}";
            }

            noButton.onclick = function () {
                overlay.style.display = "none";
                modal.style.display = "none";
            };
        }
    </script>
</body>
</html>
