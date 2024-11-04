<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <spring:message var="pageTitle" code="submit.song.title"/>
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
            <div class="mod-form-container">
                <h1 class="mod-form-title">
                    <spring:message code="submit.song.title"/>
                </h1>

                <c:url var="postURL" value="${postUrl}" />
                <form:form modelAttribute="modSongForm" action="${postURL}" method="post">
                    <input name="id" type="hidden" value="${modSongForm.id}"/>
                    <input name="albumId" type="hidden" value="${modSongForm.albumId}"/>

                    <div class="mod-form">
                        <div class="element-details-container">
                            <div>
                                <label class="mod-label">
                                    <spring:message code="submit.song.title.label"/>:
                                </label>
                                <form:errors path="title" cssClass="form-error"/>
                                <form:input path="title" type="text" cssClass="form-control" required="true" class="mod-input"/>
                            </div>

                            <div>
                                <label class="mod-label">
                                    <spring:message code="submit.song.duration.label"/>:
                                </label>
                                <form:errors path="duration" cssClass="form-error"/>
                                <form:input path="duration" type="text" cssClass="form-control"
                                           placeholder="MM:SS - Example: 10:24 or 3:15" required="true" class="mod-input"/>
                            </div>

                            <div>
                                <label class="mod-label">
                                    <spring:message code="submit.song.track.number.label"/>:
                                </label>
                                <form:errors path="trackNumber" cssClass="form-error"/>
                                <form:input path="trackNumber" type="number" cssClass="form-control" required="true" class="mod-input"/>
                            </div>
                        </div>
                    </div>

                    <!-- Delete option for existing songs -->
                    <c:if test="${songId != null}">
                        <c:url var="deleteUrl" value="/mod/delete/song/${songId}"/>
                        <button type="button" onclick="deleteSong()" class="btn btn-danger" style="margin-left: auto">
                            <spring:message code="button.delete.song"/>
                        </button>
                    </c:if>

                    <div class="form-actions">
                        <!-- Cancel Button -->
                        <c:if test="${songId == null}">
                            <c:url value="/mod" var="cancel_url" />
                        </c:if>
                        <c:if test="${songId != null}">
                            <c:url value="/song/${songId}" var="cancel_url" />
                        </c:if>
                        <a href="${cancel_url}" class="btn btn-secondary">
                            <spring:message code="button.cancel"/>
                        </a>

                        <!-- Submit Button -->
                        <button type="submit" class="btn btn-primary">
                            <spring:message code="submit.song.button"/>
                        </button>
                    </div>
                </form:form>
            </div>

            <!-- Confirmation Modal -->
            <spring:message var="confirmation_text" code="confirmation.window.song.message"/>
            <jsp:include page="/WEB-INF/jsp/components/confirmation-window.jsp">
                <jsp:param name="message" value="${confirmation_text}"/>
                <jsp:param name="id" value="Song"/>
            </jsp:include>
        </main>
    </div>

    <script>
        function deleteSong() {
            // Get the modal and buttons
            var overlay = document.getElementById("modalOverlaySong");
            var modal = document.getElementById("confirmationModalSong");
            var yesButton = document.getElementById("modalYesSong");
            var noButton = document.getElementById("modalNoSong");

            // Show the modal
            overlay.style.display = "block";
            modal.style.display = "block";

            // Handle the Yes button click
            yesButton.onclick = function () {
                window.location.href = "${deleteUrl}";
            }

            // Handle the No button click (just close the modal)
            noButton.onclick = function () {
                overlay.style.display = "none";
                modal.style.display = "none";
            };
        }
    </script>
</body>
</html>