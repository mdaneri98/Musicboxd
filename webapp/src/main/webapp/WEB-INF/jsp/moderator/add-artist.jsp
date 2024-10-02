<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>

    <spring:message var="pageTitle" text="Submit Artist"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${pageTitle}"/>
    </jsp:include>

    <c:url var="css" value="/static/css/moderator.css" />
    <link rel="stylesheet" href="${css}">
</head>
<body>
<div>
    <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
        <jsp:param name="loggedUserImgId" value="${loggedUser.imgId}"/>
        <jsp:param name="moderator" value="${loggedUser.moderator}"/>
    </jsp:include>
</div>
<div class="container form-container">
    <h1>Submit Artist</h1>

    <c:url var="postURL" value="${postUrl}" />
    <form:form id="artistForm" modelAttribute="modArtistForm" action="${postURL}" method="post" enctype="multipart/form-data">
        <!-- Artist Information -->
        <div class="container">
            <div class="info-container">
                <!-- Image Preview -->
                <c:if test="${artistId == null}">
                    <c:url var="artistImgURL" value="/images/1"/>
                </c:if>
                <c:if test="${artistId != null}">
                    <c:url var="artistImgURL" value="/images/${artistImgId}"/>
                </c:if>
                <form:input path="artistImage" id="artistImageInput" type="file" accept=".jpg,.jpeg,.png" style="display: none;" onchange="previewImage(event,0)"/>
                <img id="imagePreview-0" src="${artistImgURL}" class="primary-image" style="cursor: pointer;" onclick="document.getElementById('artistImageInput').click();"/>

                <div class="data-container element-details-container">
                    <div>
                        <label>Name:
                            <form:errors path="name" cssClass="error" element="p" cssStyle="color:red;"/>
                            <form:input path="name" type="text" required="true"/>
                        </label>
                    </div>
                    <div>
                        <label>Bio:
                            <form:errors path="bio" cssClass="error" element="p" cssStyle="color:red;"/>
                            <form:textarea path="bio" rows="5" required="true"/>
                        </label>
                    </div>
                </div>
            </div>
            <input name="deleted" id="deletedArtist" type="hidden" value="false"/>
            <c:if test="${artistId != null}">
                <button type="button" class="remove-button" style="margin-left: auto" onclick="deleteArtist()">Delete Artist</button>
            </c:if>
        </div>

        <!-- Album List Section -->
        <h2>Albums</h2>
        <div id="AlbumContainer">
            <c:forEach items="${modArtistForm.albums}" var="album" varStatus="status">
                <input name="albums[${status.index}].id" type="hidden" value="${album.id}">
                <div id="album-${status.index}" class="info-container sub-element-container">
                    <!-- Image -->
                    <c:if test="${album.id == null}">
                        <c:url var="albumImgURL" value="/images/1"/>
                    </c:if>
                    <c:if test="${album.id != null}">
                        <c:url var="albumImgURL" value="/images/${albumImgId[status.index].longValue()}" />
                    </c:if>
                    <img id="imagePreview-${status.index+1}" src="${albumImgURL}" class="sub-element-image-preview" style="cursor: pointer;" onclick="document.getElementById('albumImageInput${status.index}').click();"/>
                    <form:input path="albums[${status.index}].albumImage" id="albumImageInput${status.index}" type="file" accept=".jpg,.jpeg,.png" style="display: none;" onchange="previewImage(event,${status.index+1})"/>

                    <div class="data-container element-details-container">
                        <label>Title:
                            <form:input path="albums[${status.index}].title" type="text" required="true"/>
                        </label>
                        <label>Genre:
                            <form:input path="albums[${status.index}].genre" type="text" required="true"/>
                        </label>
                    </div>
                    <!-- Remove button for existing albums -->
                    <button type="button" class="remove-button" onclick="removeAlbum(document.getElementById('album-' + ${status.index}))">Remove Album</button>
                </div>
                <!-- Flag deleted albums -->
                <input name="albums[${status.index}].deleted" id="deletedAlbum-${status.index}" type="hidden" value="false"/>
            </c:forEach>
        </div>

        <!-- Add Album Button -->
        <div>
            <c:url var="defaultImgURL" value="/images/1"/>
            <button type="button" id="addAlbumButton" onclick="addAlbum()">+ Add Album</button>
        </div>

        <br><br>
        <div style="display: flex">
            <!-- Cancel Button -->
            <c:if test="${artistId == null}">
                <c:url value="/mod" var="cancel_url" />
            </c:if>
            <c:if test="${artistId != null}">
                <c:url value="/artist/${artistId}" var="cancel_url" />
            </c:if>
            <a href="${cancel_url}">
                <button type="button">Cancel</button>
            </a>

            <!-- Confirm Button -->
            <button type="submit" style="margin-left: auto">Submit Artist</button>
        </div>

    </form:form>

    <script>
        var albumIndex = ${modArtistForm.albums.size()};
        var albumCount = ${modArtistForm.albums.size()};
        var maxAlbums = 10;
        function addAlbum() {
            var container = document.getElementById("AlbumContainer");

            var newAlbumDiv = document.createElement("div");
            newAlbumDiv.setAttribute("id", "album-" + albumIndex);
            newAlbumDiv.setAttribute("class", "info-container sub-element-container");

            var imagePreview = document.createElement("img");
            imagePreview.setAttribute("id", "imagePreview-" + (albumIndex + 1));
            imagePreview.setAttribute("src", "${defaultImgURL}");
            imagePreview.setAttribute("class", "sub-element-image-preview");
            imagePreview.setAttribute("style", "cursor: pointer;");
            imagePreview.setAttribute("onclick", "document.getElementById('albumImageInput" + albumIndex + "').click();");
            newAlbumDiv.appendChild(imagePreview);

            var imageInput = document.createElement("input");
            imageInput.setAttribute("name", "albums[" + albumIndex + "].albumImage");
            imageInput.setAttribute("id", "albumImageInput" + albumIndex);
            imageInput.setAttribute("type", "file");
            imageInput.setAttribute("accept", ".jpg,.jpeg,.png");
            imageInput.setAttribute("style", "display: none;");
            imageInput.setAttribute("onchange", "previewImage(event," + (albumIndex + 1) + ")");
            newAlbumDiv.appendChild(imageInput);

            var newAlbumDataDiv = document.createElement("div");
            newAlbumDataDiv.setAttribute("class", "data-container element-details-container");

            var titleLabel = document.createElement("label");
            titleLabel.textContent = "Title: ";

            var titleInput = document.createElement("input");
            titleInput.setAttribute("name", "albums[" + albumIndex + "].title");
            titleInput.setAttribute("type", "text");
            titleInput.setAttribute("required", "true");
            titleLabel.appendChild(titleInput);

            newAlbumDataDiv.appendChild(titleLabel);

            var genreLabel = document.createElement("label");
            genreLabel.textContent = "Genre: ";

            var genreInput = document.createElement("input");
            genreInput.setAttribute("name", "albums[" + albumIndex + "].genre");
            genreInput.setAttribute("type", "text");
            genreInput.setAttribute("required", "true");
            genreLabel.appendChild(genreInput);

            newAlbumDataDiv.appendChild(genreLabel);

            newAlbumDiv.appendChild(newAlbumDataDiv);

            var removeButton = document.createElement("button");
            removeButton.textContent = "Remove Album";
            removeButton.setAttribute("type", "button");
            removeButton.setAttribute("class", "remove-button");
            removeButton.onclick = function() {
                removeAlbum(newAlbumDiv);
            }
            newAlbumDiv.appendChild(removeButton);

            var deletedInput = document.createElement("input");
            deletedInput.setAttribute("id", "deletedAlbum-" + albumIndex);
            deletedInput.setAttribute("name", "albums[" + albumIndex + "].deleted")
            deletedInput.setAttribute("type", "hidden");
            deletedInput.setAttribute("value", "false");
            container.appendChild(deletedInput);

            container.appendChild(newAlbumDiv);

            albumIndex++;
            albumCount++;

            // Update the button's enabled/disabled state
            toggleAddButton();
        }

        function deleteArtist() {
            // Get the modal and buttons
            var modal = document.getElementById("confirmationModal");
            var yesButton = document.getElementById("modalYes");
            var noButton = document.getElementById("modalNo");

            // Show the modal
            modal.style.display = "block";

            // Handle the Yes button click
            yesButton.onclick = function() {
                var deletedInput = document.getElementById("deletedArtist");
                deletedInput.setAttribute("value", "true");

                // Hide modal
                modal.style.display = "none";

                // Submit Form
                document.getElementById("artistForm").submit();
            }

            // Handle the No button click (just close the modal)
            noButton.onclick = function() {
                // Hide the modal without taking any action
                modal.style.display = "none";
            };
        }

        function removeAlbum(albumDiv) {
            // Get the modal and buttons
            var modal = document.getElementById("confirmationModal");
            var yesButton = document.getElementById("modalYes");
            var noButton = document.getElementById("modalNo");

            // Show the modal
            modal.style.display = "block";

            // Handle the Yes button click
            yesButton.onclick = function() {
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
                modal.style.display = "none";

                // Update the button's enabled/disabled state
                toggleAddButton();
            }

            // Handle the No button click (just close the modal)
            noButton.onclick = function() {
                modal.style.display = "none"; // Hide the modal without taking any action
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
</div>
<jsp:include page="/WEB-INF/jsp/components/confirmation-window.jsp">
    <jsp:param name="message" value="Are you sure you want to delete this?"/>
</jsp:include>
</body>
</html>


