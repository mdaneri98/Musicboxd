<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
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
<div class="container">
    <h1>Submit an Artist</h1>

    <c:url var="postUrl" value="/mod/add/artist" />
    <form:form modelAttribute="modArtistForm" action="${postUrl}" method="post" enctype="multipart/form-data">
        <!-- Artist Information -->
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
        <div>
            <label>Image:
                <form:errors path="artistImage" cssClass="error" element="p" cssStyle="color:red;"/>
                <form:input path="artistImage" type="file" accept=".jpg,.jpeg,.png" required="true" onchange="previewImage(event,0)"/>
                <!-- Image Preview -->
                <img id="imagePreview-0" class="element-image-preview" style="display:none;"/>
            </label>
        </div>

        <!-- Album List Section -->
        <h2>Albums</h2>
        <div id="AlbumContainer" class="element-table">
            <c:forEach items="${modArtistForm.albums}" var="album" varStatus="status">
                <div id="album-${status.index}" class="element-row">
                    <form:input path="albums[${status.index}].title" type="text" placeholder="Title" required="true" class="element-field"/>
                    <form:input path="albums[${status.index}].genre" type="text" placeholder="Genre" required="true" class="element-field"/>
                    <label class="element-field">Album Image:</label>
                    <img id="imagePreview-${status.index+1}" class="sub-element-image-preview" style="display:none;"/>
                    <form:input path="albums[${status.index}].albumImage" type="file" required="true" accept=".jpg,.jpeg,.png" class="element-field" onchange="previewImage(event,${status.index+1})"/>
                    <!-- Remove button for existing albums -->
                    <button type="button" class="remove-button" onclick="removeAlbum(${status.index})">Remove Album</button>
                </div>
            </c:forEach>
        </div>

        <!-- Add Album Button -->
        <div>
            <button type="button" id="addAlbumButton" onclick="addAlbum()">+ Add Album</button>
        </div>

        <br><br>
        <div>
            <button type="submit">Submit Artist</button>
        </div>
    </form:form>

    <script>
        var albumIndex = 0;
        var albumCount = 0;
        var maxAlbums = 3;
        function addAlbum() {
            var container = document.getElementById("AlbumContainer");
            container.setAttribute("class", "element-table");

            var newAlbumDiv = document.createElement("div");
            newAlbumDiv.setAttribute("class", "element-row");

            var titleInput = document.createElement("input");
            titleInput.setAttribute("type", "text");
            titleInput.setAttribute("name", "albums[" + albumIndex + "].title");
            titleInput.setAttribute("placeholder", "Title");
            titleInput.setAttribute("required", "true");
            titleInput.setAttribute("class", "element-field");
            newAlbumDiv.appendChild(titleInput);

            var genreInput = document.createElement("input");
            genreInput.setAttribute("type", "text");
            genreInput.setAttribute("name", "albums[" + albumIndex + "].genre");
            genreInput.setAttribute("placeholder", "Genre");
            genreInput.setAttribute("required", "true");
            genreInput.setAttribute("class", "element-field");
            newAlbumDiv.appendChild(genreInput);

            var ImageLabel = document.createElement("label");
            ImageLabel.setAttribute("class", "element-field");
            ImageLabel.textContent = "Image: ";
            newAlbumDiv.appendChild(ImageLabel);

            var ImagePreview = document.createElement("img");
            ImagePreview.setAttribute("id", "imagePreview-" + (albumIndex + 1));
            ImagePreview.setAttribute("class", "sub-element-image-preview");
            ImagePreview.setAttribute("style", "display:none;");
            newAlbumDiv.appendChild(ImagePreview);

            var ImageInput = document.createElement("input");
            ImageInput.setAttribute("type", "file");
            ImageInput.setAttribute("name", "albums[" + albumIndex + "].albumImage");
            ImageInput.setAttribute("required", "true");
            ImageInput.setAttribute("accept", ".jpg,.jpeg,.png");
            ImageInput.setAttribute("class", "element-field");
            ImageInput.setAttribute("onchange", "previewImage(event," + (albumIndex + 1) + ")")
            newAlbumDiv.appendChild(ImageInput);

            var removeButton = document.createElement("button");
            removeButton.textContent = "Remove Album";
            removeButton.setAttribute("type", "button");
            removeButton.setAttribute("class", "remove-button");
            removeButton.onclick = function () {
                container.removeChild(newAlbumDiv);
            };
            newAlbumDiv.appendChild(removeButton);

            container.appendChild(newAlbumDiv);

            albumIndex++;
            albumCount++;

            // Update the button's enabled/disabled state
            //toggleAddButton(albumCount);
        }

        function removeAlbum(index) {
            var container = document.getElementById("AlbumContainer");
            var albumDiv = document.getElementById("album-" + index);

            if (albumDiv) {
                container.removeChild(albumDiv);
                albumCount--;  // Decrease the count when an album is removed
            }

            // Update the button's enabled/disabled state
            //toggleAddButton(albumCount);
        }

        /* Disables but does not enable the button

        function toggleAddButton(count) {
            var addAlbumButton = document.getElementById("addAlbumButton");

            if (count >= maxAlbums) {
                addAlbumButton.disabled = true;  // Disable the button when the max is reached
            } else {
                addAlbumButton.disabled = false; // Re-enable the button if the count is below the max
            }
        }
        */

        // Previews Image inserted
        function previewImage(event,index) {
            const file = event.target.files[0];
            const preview = document.getElementById('imagePreview-' + index);

            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    preview.src = e.target.result;
                    preview.style.display = 'block'; // Show the image element
                }
                reader.readAsDataURL(file); // Read the file and convert it to a data URL
            } else {
                preview.style.display = 'none'; // Hide the preview if no image is selected
            }
        }

    </script>
</div>
</body>
</html>


