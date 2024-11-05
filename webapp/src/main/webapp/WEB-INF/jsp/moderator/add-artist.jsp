<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>

    <spring:message var="pageTitle" code="submit.artist.heading"/>
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
                <spring:message code="submit.artist.title"/>
            </h1>

            <c:url var="postURL" value="${postUrl}" />
            <form:form id="artistForm" modelAttribute="modArtistForm" action="${postURL}" method="post" enctype="multipart/form-data">
                <!-- Artist Information -->
                <div class="mod-form">
                    <!-- Image Preview -->
                    <c:if test="${artistId == null}">
                        <c:url var="artistImgURL" value="/images/${defaultImgId}"/>
                    </c:if>
                    <c:if test="${artistId != null}">
                        <c:url var="artistImgURL" value="/images/${modArtistForm.artistImgId}"/>
                    </c:if>
                    <img id="imagePreview-0" src="${artistImgURL}" class="entity-image mod-editable-image" onclick="document.getElementById('artistImageInput').click();"/>
                    <form:input path="artistImage" id="artistImageInput" type="file" accept=".jpg,.jpeg,.png" style="display: none;" onchange="previewImage(event,0)"/>

                    <input name="id" type="hidden" value="${modArtistForm.id}"/>
                    <input name="artistImgId" type="hidden" value="${modArtistForm.artistImgId}"/>
                    <div class="mod-entity-details">
                        <div>
                            <label class="mod-label"><spring:message code="submit.artist.name.label"/>:
                                <form:errors path="name" class="error" style="color:red;"/>
                                <form:input path="name" type="text" required="true" class="mod-input"/>
                            </label>
                        </div>
                        <div>
                            <label class="mod-label"><spring:message code="submit.artist.desc.label"/>:
                                <form:errors path="bio" class="error" style="color:red;"/>
                                <form:textarea path="bio" rows="5" required="true" class="mod-input"/>
                            </label>
                        </div>
                    </div>
                </div>

                <input name="deleted" id="deletedArtist" type="hidden" value="false"/>
                <c:if test="${artistId != null}">
                    <c:url var="deleteUrl" value="/mod/delete/artist/${artistId}"/>
                    <a onclick="deleteArtist()">
                        <button type="button" class="btn btn-danger" style="margin-left: auto">
                            <spring:message code="button.delete.artist"/>
                        </button>
                    </a>
                </c:if>

                <!-- Album List Section -->
                <h2><spring:message code="submit.artist.albums.title"/></h2>
                <div id="AlbumContainer">
                    <c:forEach items="${modArtistForm.albums}" var="album" varStatus="status">
                        <div id="album-${status.index}" class="sub-element-container">
                            <!-- Image -->
                            <c:url var="albumImgURL" value="/images/${album.albumImageId}"/>
                            <img id="imagePreview-${status.index+1}" src="${albumImgURL}" class="sub-element-image-preview mod-editable-image" onclick="document.getElementById('albumImageInput${status.index}').click();"/>
                            <!-- Hidden input to store base64-encoded image -->
                            <form:errors path="albums[${status.index}].albumImage" class="error" style="color:red;"/>
                            <form:input path="albums[${status.index}].albumImage" id="albumImageInput${status.index}" type="file" accept=".jpg,.jpeg,.png" style="display: none;" onchange="previewImage(event,${status.index+1})"/>

                            <div class="mod-entity-details">
                                <label class="mod-label"><spring:message code="submit.album.title.label"/>:
                                    <form:errors path="albums[${status.index}].title" class="error" style="color:red;"/>
                                    <form:input path="albums[${status.index}].title" type="text" required="true" class="mod-input"/>
                                </label>
                                <label class="mod-label"><spring:message code="submit.album.genre.label"/>:
                                    <form:errors path="albums[${status.index}].genre" class="error" style="color:red;"/>
                                    <form:input path="albums[${status.index}].genre" type="text" required="true" class="mod-input"/>
                                </label>
                                <label class="mod-label"><spring:message code="submit.album.release.date.label"/>:
                                    <form:errors path="albums[${status.index}].releaseDate" class="error" style="color:red;"/>
                                    <form:input path="albums[${status.index}].releaseDate" type="date" required="true" class="mod-input"/>
                                </label>
                            </div>
                            <!-- Remove button for existing albums -->
                            <button type="button" class="btn btn-danger" onclick="removeAlbum(document.getElementById('album-' + ${status.index}))">
                                <spring:message code="button.remove.album"/>
                            </button>
                        </div>
                        <!-- Save key data -->
                        <input name="albums[${status.index}].id" type="hidden" value="${album.id}"/>
                        <input name="albums[${status.index}].artistId" type="hidden" value="${album.artistId}"/>
                        <input name="albums[${status.index}].albumImageId" type="hidden" value="${album.albumImageId}"/>
                        <!-- Flag deleted albums -->
                        <input name="albums[${status.index}].deleted" id="deletedAlbum-${status.index}" type="hidden" value="false"/>
                    </c:forEach>
                </div>

                <!-- Add Album Button -->
                <div>
                    <c:url var="defaultImgURL" value="/images/${defaultImgId}"/>
                    <button type="button" id="addAlbumButton" class="btn btn-secondary" onclick="addAlbum()">
                        <spring:message code="button.add.album"/>
                    </button>
                </div>

                <div class="form-actions">
                    <!-- Cancel Button -->
                    <c:if test="${artistId == null}">
                        <c:url value="/mod" var="cancel_url" />
                    </c:if>
                    <c:if test="${artistId != null}">
                        <c:url value="/artist/${artistId}" var="cancel_url" />
                    </c:if>
                    <a href="${cancel_url}">
                        <button type="button" class="btn btn-secondary">
                            <spring:message code="button.cancel"/>
                        </button>
                    </a>

                    <!-- Submit Button -->
                    <button type="submit" class="btn btn-primary">
                        <spring:message code="submit.artist.button"/>
                    </button>
                </div>
            </form:form>
        </div>

        <!-- Confirmation Modals -->
        <spring:message var="confirmation_text" code="confirmation.window.artist.message"/>
        <jsp:include page="/WEB-INF/jsp/components/confirmation-window.jsp">
            <jsp:param name="message" value="${confirmation_text}"/>
            <jsp:param name="id" value="Artist"/>
        </jsp:include>

        <spring:message var="confirmation_text" code="confirmation.window.album.message"/>
        <jsp:include page="/WEB-INF/jsp/components/confirmation-window.jsp">
            <jsp:param name="message" value="${confirmation_text}"/>
            <jsp:param name="id" value="Album"/>
        </jsp:include>

                    
        <div class="footer-placeholder">
            <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
        </div>
    </main>
</div>

<script>
    var albumIndex = ${modArtistForm.albums.size()};
    var albumCount = ${modArtistForm.albums.size()};
    var maxAlbums = 40;

    function addAlbum() {
        // divs
        var container = document.getElementById("AlbumContainer");

        var newAlbumDiv = document.createElement("div");
        newAlbumDiv.setAttribute("id", "album-" + albumIndex);
        newAlbumDiv.setAttribute("class", "sub-element-container");

        var newAlbumDataDiv = document.createElement("div");
        newAlbumDataDiv.setAttribute("class", "mod-entity-details");

        // Image
        var imagePreview = document.createElement("img");
        imagePreview.setAttribute("id", "imagePreview-" + (albumIndex + 1));
        imagePreview.setAttribute("src", "${defaultImgURL}");
        imagePreview.setAttribute("class", "sub-element-image-preview mod-editable-image");
        imagePreview.setAttribute("onclick", "document.getElementById('albumImageInput" + albumIndex + "').click();");

        // Labels
        var titleLabel = document.createElement("label");
        titleLabel.setAttribute("class", "mod-label");
        titleLabel.textContent = "Title: ";

        var genreLabel = document.createElement("label");
        genreLabel.setAttribute("class", "mod-label");
        genreLabel.textContent = "Genre: ";

        var releaseDateLabel = document.createElement("label");
        releaseDateLabel.setAttribute("class", "mod-label");
        releaseDateLabel.textContent = "Release Date: ";

        // Inputs
        var imageInput = document.createElement("input");
        imageInput.setAttribute("name", "albums[" + albumIndex + "].albumImage");
        imageInput.setAttribute("id", "albumImageInput" + albumIndex);
        imageInput.setAttribute("type", "file");
        imageInput.setAttribute("accept", ".jpg,.jpeg,.png");
        imageInput.setAttribute("style", "display: none;");
        imageInput.setAttribute("onchange", "previewImage(event," + (albumIndex + 1) + ")");

        var titleInput = document.createElement("input");
        titleInput.setAttribute("name", "albums[" + albumIndex + "].title");
        titleInput.setAttribute("type", "text");
        titleInput.setAttribute("required", "true");
        titleInput.setAttribute("placeholder", "Title of the album");
        titleInput.setAttribute("class", "mod-input");

        var genreInput = document.createElement("input");
        genreInput.setAttribute("name", "albums[" + albumIndex + "].genre");
        genreInput.setAttribute("type", "text");
        genreInput.setAttribute("required", "true");
        genreInput.setAttribute("placeholder", "Genre of the album");
        genreInput.setAttribute("class", "mod-input");

        var releaseDateInput = document.createElement("input");
        releaseDateInput.setAttribute("name", "albums[" + albumIndex + "].releaseDate");
        releaseDateInput.setAttribute("type", "date");
        releaseDateInput.setAttribute("required", "true");
        releaseDateInput.setAttribute("class", "mod-input");

        var deletedInput = document.createElement("input");
        deletedInput.setAttribute("id", "deletedAlbum-" + albumIndex);
        deletedInput.setAttribute("name", "albums[" + albumIndex + "].deleted")
        deletedInput.setAttribute("type", "hidden");
        deletedInput.setAttribute("value", "false");

        // Errors
        var titleError = document.createElement("p");
        titleError.setAttribute("id", "error-title-" + albumIndex);
        titleError.setAttribute("class", "error");
        titleError.style.color = "red";

        var genreError = document.createElement("p");
        genreError.setAttribute("id", "error-genre-" + albumIndex);
        genreError.setAttribute("class", "error");
        genreError.style.color = "red";

        var releaseDateError = document.createElement("p");
        releaseDateError.setAttribute("id", "error-releaseDate-" + albumIndex);
        releaseDateError.setAttribute("class", "error");
        releaseDateError.style.color = "red";

        // Remove Button
        var removeButton = document.createElement("button");
        removeButton.textContent = "Remove Album";
        removeButton.setAttribute("type", "button");
        removeButton.setAttribute("class", "btn btn-danger");
        removeButton.onclick = function() {
            removeAlbum(newAlbumDiv);
        }

        // Building structure
        titleLabel.appendChild(titleInput);
        genreLabel.appendChild(genreInput);
        releaseDateLabel.appendChild(releaseDateInput);

        newAlbumDataDiv.appendChild(titleLabel);
        newAlbumDataDiv.appendChild(genreLabel);
        newAlbumDataDiv.appendChild(releaseDateLabel);

        newAlbumDiv.appendChild(imagePreview);
        newAlbumDiv.appendChild(imageInput);
        newAlbumDiv.appendChild(newAlbumDataDiv);
        newAlbumDiv.appendChild(removeButton);

        container.appendChild(deletedInput);
        container.appendChild(newAlbumDiv);

        /* Final result looks like this
        |<container>
        |    |<newAlbumDiv>
        |    |    |<imagePreview>
        |    |    |<imageInput>
        |    |    |<newAlbumDataDiv>
        |    |    |    |<titleLabel>
        |    |    |    |    <titleInput>
        |    |    |    |<genreLabel>
        |    |    |    |    <genreInput>
        |    |    |    |<releaseDateLabel>
        |    |    |    |    <releaseDateInput>
        |    |    |<removeButton>
        |    |<deletedInput>
        */

        albumIndex++;
        albumCount++;

        // Update the button's enabled/disabled state
        toggleAddButton();
    }

    function deleteArtist() {
        // Get the modal and buttons
        var overlay = document.getElementById("modalOverlayArtist");
        var modal = document.getElementById("confirmationModalArtist");
        var yesButton = document.getElementById("modalYesArtist");
        var noButton = document.getElementById("modalNoArtist");

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

    function removeAlbum(albumDiv) {
        // Get the modal and buttons
        var overlay = document.getElementById("modalOverlayAlbum");
        var modal = document.getElementById("confirmationModalAlbum");
        var yesButton = document.getElementById("modalYesAlbum");
        var noButton = document.getElementById("modalNoAlbum");

        // Show the modal
        overlay.style.display = "block";
        modal.style.display = "block";

        // Handle the Yes button click
        yesButton.onclick = function () {
            // Proceed with album removal if the user pressed 'Yes'
            var container = document.getElementById("AlbumContainer");

            if (albumDiv) {
                var id = albumDiv.getAttribute("id");
                var removedAlbumIndex = id.split('-')[1];

                // Add a hidden input field to send this info to the server
                var deletedInput = document.getElementById("deletedAlbum-" + removedAlbumIndex);
                deletedInput.setAttribute("value", "true");

                container.removeChild(albumDiv);

                // Decrease the count when an album is removed
                albumCount--;
            }

            // Hide the modal after removing the album
            overlay.style.display = "none";
            modal.style.display = "none";

            // Update the button's enabled/disabled state
            toggleAddButton();
        }

        // Handle the No button click (just close the modal)
        noButton.onclick = function () {
            overlay.style.display = "none";
            modal.style.display = "none";
        };
    }

    // Previews Image inserted
    function previewImage(event,index) {
        const file = event.target.files[0];
        const preview = document.getElementById('imagePreview-' + index);

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

    function toggleAddButton() {
        var addAlbumButton = document.getElementById("addAlbumButton");
        //Disables button if max albums has been reached
        addAlbumButton.disabled = albumCount >= maxAlbums;
    }
</script>
</body>
</html>


