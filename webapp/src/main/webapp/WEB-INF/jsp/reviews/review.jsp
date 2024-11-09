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
            <jsp:param name="unreadNotificationCount" value="${loggedUser.unreadNotificationCount}"/>
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
            
            <!-- Tabs Navigation -->
            <div class="section-header-home">
                <div class="tabs">
                    <c:if test="${!likesActive}">
                        <span class="tab active"><spring:message code="comments.title"/></span>
                        <c:url var="likesUrl" value="/review/${review.id}?page=likes"/>
                        <a href="${likesUrl}">
                            <span class="tab"><spring:message code="label.likes"/></span>
                        </a>
                    </c:if>
                    <c:if test="${likesActive}">
                        <c:url var="commentsUrl" value="/review/${review.id}"/>
                        <a href="${commentsUrl}">
                            <span class="tab"><spring:message code="comments.title"/></span>
                        </a>
                        <span class="tab active"><spring:message code="label.likes"/></span>
                    </c:if>
                </div>
            </div>

            <!-- Tab Content -->
            <c:choose>
                <c:when test="${likesActive}">
                    <!-- Likes Tab Content -->
                    <div class="users-grid">
                        <c:forEach var="user_item" items="${likedUsers}">
                            <jsp:include page="/WEB-INF/jsp/components/user_card.jsp">
                                <jsp:param name="imgId" value="${user_item.image.id}"/>
                                <jsp:param name="username" value="@${user_item.username}"/>
                                <jsp:param name="name" value="${user_item.name}"/>
                                <jsp:param name="bio" value="${user_item.bio}"/>
                                <jsp:param name="followersAmount" value="${user_item.followersAmount}"/>
                                <jsp:param name="followingAmount" value="${user_item.followingAmount}"/>
                                <jsp:param name="reviewAmount" value="${user_item.reviewAmount}"/>
                                <jsp:param name="verified" value="${user_item.verified}"/>
                                <jsp:param name="moderator" value="${user_item.moderator}"/>
                                <jsp:param name="id" value="${user_item.id}"/>
                            </jsp:include>
                        </c:forEach>
                    </div>

                    <c:if test="${empty likedUsers}">
                        <p class="no-results">
                            <spring:message code="label.no.results"/>
                        </p>
                    </c:if>

                    <!-- Pagination for Likes -->
                    <div class="pagination">
                        <c:url value="/review/${review.id}?page=likes&pageNum=${pageNum + 1}" var="nextPage" />
                        <c:url value="/review/${review.id}?page=likes&pageNum=${pageNum - 1}" var="prevPage" />
                        <c:if test="${showPrevious}">
                            <a href="${prevPage}" class="btn btn-secondary">
                                <spring:message code="button.previous.page"/>
                            </a>
                        </c:if>
                        <c:if test="${showNext}">
                            <a href="${nextPage}" class="btn btn-secondary">
                                <spring:message code="button.next.page"/>
                            </a>
                        </c:if>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Comments Tab Content -->
                    <section class="comments-section">
                        <h3><spring:message code="comments.title"/></h3>

                        <!-- Comment Form -->
                        <c:url value='/review/${review.id}/comment' var="postComment"/>
                        <form:form modelAttribute="commentForm" method="POST" action="${postComment}" class="comment-form">
                            <form:textarea path="content" maxlength="500" class="form-control comment-input" 
                                         placeholder="Write a comment..."/>
                            <form:errors path="content" cssClass="form-error" />
                            <c:choose>
                                <c:when test="${loggedUser.id == 0}">
                                    <c:url value="/user/login" var="login"/>
                                    <a href="${login}" class="btn btn-primary">
                                        <spring:message code="label.login.comment"/>
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <button type="submit" class="btn btn-primary">
                                        <spring:message code="comments.submit"/>
                                    </button>
                                </c:otherwise>
                            </c:choose>
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
                                            <div class="user-details">
                                                <span class="comment-username">@<c:out value="${comment.user.username}"/></span>
                                                <div class="user-badges">
                                                    <c:if test="${comment.user.verified}">
                                                        <span class="badge badge-verified">
                                                            <spring:message code="label.verified" />
                                                        </span>
                                                    </c:if>
                                                    <c:if test="${comment.user.moderator}">
                                                        <span class="badge badge-moderator">
                                                            <spring:message code="label.moderator" />
                                                        </span>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </a>
                                        
                                        <c:if test="${loggedUser.id == comment.user.id || loggedUser.moderator}">
                                            <c:url value='/review/${review.id}/comment/' var="baseCommentUrl"/>
                                            <button type="button" onclick="deleteComment(${comment.id})" class="btn btn-danger btn-sm">
                                                <spring:message code="comments.delete"/>
                                            </button>
                                        </c:if>
                                    </div>
                                    <span class="comment-date"><c:out value="${comment.timeAgo}"/></span>
                                    <p class="comment-content"><c:out value="${comment.content}"/></p>
                                </div>
                            </c:forEach>
                        </div>
                    </section>
                    <div class="pagination">
                        <c:url value="/review/${review.id}?page=comments&pageNum=${pageNum + 1}" var="nextPage" />
                        <c:url value="/review/${review.id}?page=comments&pageNum=${pageNum - 1}" var="prevPage" />
                        <c:if test="${showPrevious}">
                            <a href="${prevPage}" class="btn btn-secondary">
                                <spring:message code="button.previous.page"/>
                            </a>
                        </c:if>
                        <c:if test="${showNext}">
                            <a href="${nextPage}" class="btn btn-secondary">
                                <spring:message code="button.next.page"/>
                            </a>
                        </c:if>
                    </div>
                </c:otherwise>
            </c:choose>

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
                window.location.href = "${baseCommentUrl}" + commentId + "/delete";
            }

            noButton.onclick = function () {
                overlay.style.display = "none";
                modal.style.display = "none";
            };
        }
    </script>
</body>
</html>
