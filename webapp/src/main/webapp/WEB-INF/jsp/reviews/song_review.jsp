<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <spring:message var="pageTitle" code="page.song.review.title"/>
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
            <h1 class="page-title"><spring:message code="label.make.a.review" /></h1>

            <!-- Song Preview -->
            <c:url value="/song/${song.id}" var="songUrl" />
            <div class="review-preview">
                <a href="${songUrl}" class="review-preview-link">
                    <c:url var="imgUrl" value="/images/${album.image.id}"/>
                    <img src="${imgUrl}" alt="${song.title}" class="review-preview-image">
                    <div class="review-preview-info">
                        <h2 class="review-preview-title"><c:out value="${song.title}"/></h2>
                        <p class="review-preview-subtitle"><c:out value="${song.duration}"/></p>
                    </div>
                </a>
            </div>

            <!-- Review Form -->
            <div class="review-section">
                <c:choose>
                    <c:when test="${!edit}">
                        <c:url var="posturl" value="/song/${song.id}/reviews" />
                        <jsp:include page="/WEB-INF/jsp/components/review_form.jsp">
                            <jsp:param name="posturl" value="${posturl}"/>
                            <jsp:param name="cancelUrl" value="${songUrl}"/>
                        </jsp:include>
                    </c:when>
                    <c:otherwise>
                        <c:url var="posturl" value="/song/${song.id}/edit-review" />
                        <jsp:include page="/WEB-INF/jsp/components/review_form.jsp">
                            <jsp:param name="posturl" value="${posturl}"/>
                            <jsp:param name="cancelUrl" value="${songUrl}"/>
                        </jsp:include>
                        <c:url var="deleteUrl" value="/song/${song.id}/delete-review" />
                        <div class="delete-review-container">
                            <button type="button" onclick="deleteReview()" class="btn btn-danger">
                                <spring:message code="label.delete.review" />
                            </button>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
        </main>
    </div>

    <!-- Confirmation Modal -->
    <spring:message var="confirmation_text" code="confirmation.window.review.message"/>
    <jsp:include page="/WEB-INF/jsp/components/confirmation-window.jsp">
        <jsp:param name="message" value="${confirmation_text}"/>
        <jsp:param name="id" value="Review"/>
    </jsp:include>

    <script>
        function deleteReview() {
            var overlay = document.getElementById("modalOverlayReview");
            var modal = document.getElementById("confirmationModalReview");
            var yesButton = document.getElementById("modalYesReview");
            var noButton = document.getElementById("modalNoReview");

            overlay.style.display = "block";
            modal.style.display = "block";

            yesButton.onclick = function () {
                window.location.href = "${deleteUrl}";
            }

            noButton.onclick = function () {
                overlay.style.display = "none";
                modal.style.display = "none";
            };
        }
    </script>
</body>
</html>
