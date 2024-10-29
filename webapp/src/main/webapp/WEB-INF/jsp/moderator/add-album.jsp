<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <spring:message var="pageTitle" code="submit.album.heading"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${pageTitle}"/>
    </jsp:include>

    <c:url var="css" value="/static/css/moderator.css" />
    <link rel="stylesheet" href="${css}">
</head>
<body>
<div>
    <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
        <jsp:param name="loggedUserImgId" value="${loggedUser.image.id}"/>
        <jsp:param name="moderator" value="${loggedUser.moderator}"/>
    </jsp:include>
</div>
<div class="container form-container">
    <h1><spring:message code="submit.album.title"/></h1>

    <c:url var="postURL" value="${postUrl}" />
    <form:form id="albumForm" modelAttribute="modAlbumForm" action="${postURL}" method="post" enctype="multipart/form-data">
        <!-- Album Information -->
        <div class="container">
            <div class="info-container">
                <!-- Image Preview -->
                <c:if test="${albumId == null}">
                    <c:url var="albumImgURL" value="/images/${defaultImgId}"/>
                </c:if>
                <c:if test="${albumId != null}">
                    <c:url var="albumImgURL" value="/images/${modAlbumForm.albumImageId}"/>
                </c:if>
                <form:input path="albumImage" id="albumImageInput" type="file" accept=".jpg,.jpeg,.png" style="display: none;" onchange="previewImage(event)"/>
                <img id="imagePreview" src="${albumImgURL}" class="primary-image" style="display: none;" onclick="document.getElementById('albumImageInput').click();" alt="image"/>

                <input name="id" type="hidden" value="${modAlbumForm.id}"/>
                <input name="artistId" type="hidden" value="${modAlbumForm.artistId}"/>
                <input name="albumImageId" type="hidden" value="${modAlbumForm.albumImageId}"/>
                <div class="data-container element-details-container">
                    <div>
                        <label><spring:message code="submit.album.title.label"/>:
                        <form:errors path="title" cssClass="error" cssStyle="color:red;"/>
                        <form:input path="title" type="text" required="true"/>
                        </label>
                    </div>
                    <div>
                        <label><spring:message code="submit.album.genre.label"/>:
                            <form:errors path="genre" cssClass="error" cssStyle="color:red;"/>
                            <form:input path="genre" type="text" required="true"/>
                        </label>
                    </div>
                    <div>
                        <label><spring:message code="submit.album.release.date.label"/>:
                            <form:errors path="releaseDate" cssClass="error" cssStyle="color:red;"/>
                            <form:input path="releaseDate" type="date" required="true"/>
                        </label>
                    </div>
                </div>
            </div>
            <input name="deleted" id="deletedAlbum" type="hidden" value="false"/>
            <c:if test="${albumId != null}">
                <c:url var="deleteUrl" value="/mod/delete/album/${albumId}"/>
                <a style="margin-left: auto" onclick="deleteAlbum()">
                    <button type="button" class="remove-button"><spring:message code="button.delete.album"/></button>
                </a>
            </c:if>
        </div>

        <!-- Song List Section -->
        <h2><spring:message code="submit.album.songs.title"/></h2>
        <div id="SongContainer">
            <c:forEach items="${modAlbumForm.songs}" var="song" varStatus="status">
                <input name="songs[${status.index}].id" type="hidden" value="${song.id}">
                <input name="songs[${status.index}].albumId" type="hidden" value="${song.albumId}"/>
                <div id="song-${status.index}" class="info-container sub-element-container">
                    <div class="data-container element-details-container song-details-container">
                        <label><spring:message code="submit.song.title.label"/>:
                            <form:errors path="songs[${status.index}].title" cssClass="error" cssStyle="color:red;"/>
                            <form:input path="songs[${status.index}].title" type="text" required="true" class="element-field"/>
                        </label>
                        <label><spring:message code="submit.song.duration.label"/>:
                            <form:errors path="songs[${status.index}].duration" cssClass="error" cssStyle="color:red;"/>
                            <form:input path="songs[${status.index}].duration" type="text" required="true" class="element-field"/>
                        </label>
                        <label><spring:message code="submit.song.track.number.label"/>:
                            <form:errors path="songs[${status.index}].trackNumber" cssClass="error" cssStyle="color:red;"/>
                            <form:input path="songs[${status.index}].trackNumber" type="number" required="true" class="element-field"/>
                        </label>
                    </div>
                    <!-- Remove button for existing songs -->
                    <button type="button" class="remove-button" onclick="removeSong(document.getElementById('song-' + ${status.index}))"><spring:message code="button.remove.song"/></button>
                </div>
                <!-- Flag deleted songs -->
                <input name="songs[${status.index}].deleted" id="deletedSong-${status.index}" type="hidden" value="false"/>
            </c:forEach>
        </div>

        <!-- Add Song Button -->
        <div>
            <c:url var="defaultImgURL" value="/images/${defaultImgId}"/>
            <button type="button" id="addSongButton" onclick="addSong()"><spring:message code="button.add.song"/></button>
        </div>

        <br><br>
        <div style="display: flex">
            <!-- Cancel Button -->
            <c:if test="${albumId == null}">
                <c:url value="/mod" var="cancel_url" />
            </c:if>
            <c:if test="${albumId != null}">
                <c:url value="/album/${albumId}" var="cancel_url" />
            </c:if>
            <a href="${cancel_url}">
                <button type="button"><spring:message code="button.cancel"/></button>
            </a>

            <!-- Submit Button -->
            <button type="submit" style="margin-left: auto"><spring:message code="submit.album.button"/></button>
        </div>
    </form:form>

    <script>
        var songIndex = ${modAlbumForm.songs.size()};
        var songCount = ${modAlbumForm.songs.size()};
        var maxSongs = 15;
        function addSong() {
            var container = document.getElementById("SongContainer");

            var newSongDiv = document.createElement("div");
            newSongDiv.setAttribute("id", "song-" + songIndex);
            newSongDiv.setAttribute("class", "info-container sub-element-container");

            var newAlbumDataDiv = document.createElement("div");
            newAlbumDataDiv.setAttribute("class", "data-container element-details-container");

            var titleLabel = document.createElement("label");
            titleLabel.textContent = "Title: ";

            var durationLabel = document.createElement("label");
            durationLabel.textContent = "Duration: ";

            var trackNumberLabel = document.createElement("label");
            trackNumberLabel.textContent = "Track Number: ";

            var titleInput = document.createElement("input");
            titleInput.setAttribute("type", "text");
            titleInput.setAttribute("name", "songs[" + songIndex + "].title");
            titleInput.setAttribute("required", "true");

            var durationInput = document.createElement("input");
            durationInput.setAttribute("type", "text");
            durationInput.setAttribute("name", "songs[" + songIndex + "].duration");
            durationInput.setAttribute("required", "true");

            var trackNumberInput = document.createElement("input");
            trackNumberInput.setAttribute("type", "number");
            trackNumberInput.setAttribute("name", "songs[" + songIndex + "].trackNumber");
            trackNumberInput.setAttribute("required", "true");

            titleLabel.appendChild(titleInput);
            durationLabel.appendChild(durationInput);
            trackNumberLabel.appendChild(trackNumberInput);

            newAlbumDataDiv.appendChild(titleLabel);
            newAlbumDataDiv.appendChild(durationLabel);
            newAlbumDataDiv.appendChild(trackNumberLabel);

            newSongDiv.appendChild(newAlbumDataDiv);

            var removeButton = document.createElement("button");
            removeButton.textContent = "Remove Song";
            removeButton.setAttribute("type", "button");
            removeButton.setAttribute("class", "remove-button");
            removeButton.onclick = function () {
                removeSong(newSongDiv);
            };
            newSongDiv.appendChild(removeButton);

            var deletedInput = document.createElement("input");
            deletedInput.setAttribute("id", "deletedSong-" + songIndex);
            deletedInput.setAttribute("name", "songs[" + songIndex + "].deleted")
            deletedInput.setAttribute("type", "hidden");
            deletedInput.setAttribute("value", "false");
            container.appendChild(deletedInput);

            container.appendChild(newSongDiv);

            songIndex++;
            songCount++;

            // Update the button's enabled/disabled state
            toggleAddButton(songCount);
        }

        function deleteAlbum() {
            // Get the modal and buttons
            var overlay = document.getElementById("confirmationModalAlbum");
            var modal = document.getElementById("confirmationModalAlbum");
            var yesButton = document.getElementById("modalYesAlbum");
            var noButton = document.getElementById("modalNoAlbum");

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

        function removeSong(songDiv) {
            // Get the modal and buttons
            var overlay = document.getElementById("modalOverlaySong");
            var modal = document.getElementById("confirmationModalSong");
            var yesButton = document.getElementById("modalYesSong");
            var noButton = document.getElementById("modalNoSong");

            // Show the modal
            overlay.style.display = "block";
            modal.style.display = "block";

            // Handle the Yes button click
            yesButton.onclick = function() {
                // Proceed with song removal if the user pressed 'Yes'
                var container = document.getElementById("SongContainer");

                if (songDiv) {
                    var id = songDiv.getAttribute("id");
                    var removedSongIndex = id.split('-')[1];

                    // Add a hidden input field to send this info to the server
                    var deletedInput = document.getElementById("deletedSong-" + removedSongIndex);
                    deletedInput.setAttribute("value", "true");

                    container.removeChild(songDiv);

                    // Decrease the count when an album is removed
                    songCount--;
                }

                // Hide the modal after removing the album
                overlay.style.display = "none";
                modal.style.display = "none";

                // Update the button's enabled/disabled state
                toggleAddButton();
            }

            // Handle the No button click (just close the modal)
            noButton.onclick = function() {
                overlay.style.display = "none";
                modal.style.display = "none";
            };
        }

        function toggleAddButton() {
            var addSongButton = document.getElementById("addSongButton");
            //Disables button if max albums has been reached
            addSongButton.disabled = songCount >= maxSongs;
        }

        // Previews Image inserted
        function previewImage(event) {
            const file = event.target.files[0];
            const preview = document.getElementById('imagePreview');

            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    preview.src = e.target.result;
                    // Show the image element
                    preview.style.display = 'block';
                }
                // Read the file and convert it to a data URL
                reader.readAsDataURL(file);
            }
        }

    </script>
</div>
<!-- Confirmation for Album -->
<spring:message var="confirmation_text" code="confirmation.window.album.message"/>
<jsp:include page="/WEB-INF/jsp/components/confirmation-window.jsp">
    <jsp:param name="message" value="${confirmation_text}"/>
    <jsp:param name="id" value="Album"/>
</jsp:include>

<!-- Confirmation for Song -->
<spring:message var="confirmation_text" code="confirmation.window.song.message"/>
<jsp:include page="/WEB-INF/jsp/components/confirmation-window.jsp">
    <jsp:param name="message" value="${confirmation_text}"/>
    <jsp:param name="id" value="Song"/>
</jsp:include>
</body>
</html>