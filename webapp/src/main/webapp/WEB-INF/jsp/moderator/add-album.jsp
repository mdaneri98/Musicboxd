<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <spring:message var="pageTitle" text="Submit an Album"/>
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
    <h1>Submit an Album</h1>

    <c:url var="postUrl" value="/mod/add/artist/${artistId}/album" />
    <form:form modelAttribute="modAlbumForm" action="${postUrl}" method="post" enctype="multipart/form-data">
        <!-- Album Information -->
        <div>
            <label>Title:
                <form:errors path="title" cssClass="error" element="p" cssStyle="color:red;"/>
                <form:input path="title" type="text" required="true"/>
            </label>
        </div>
        <div>
            <label>Genre:
                <form:errors path="genre" cssClass="error" element="p" cssStyle="color:red;"/>
                <form:input path="genre" type="text" required="true"/>
            </label>
        </div>
        <!--
        <div>
            <label>Release Date:
                <input name="releaseDate" type="datetime-local" />
                form:errors path="releaseDate" cssClass="error" />
            </label>
        </div>
        -->
        <div>
            <label>Image:
                <form:errors path="albumImage" cssClass="error" element="p" cssStyle="color:red;"/>
                <form:input path="albumImage" type="file" accept=".jpg,.jpeg,.png" required="true" onchange="previewImage(event)"/>
                <img id="imagePreview" class="element-image-preview" style="display:none;"/>
            </label>
        </div>

        <!-- Song List Section -->
        <h2>Songs</h2>
        <div id="SongContainer" class="element-table">
            <c:forEach items="${modAlbumForm.songs}" var="song" varStatus="status">
                <div id="song-${status.index}" class="element-row">
                    <form:input path="songs[${status.index}].title" type="text" placeholder="Title" required="true" class="element-field"/>
                    <form:input path="songs[${status.index}].duration" type="text" placeholder="Duration in MM:SS" required="true" class="element-field"/>
                    <form:input path="songs[${status.index}].trackNumber" type="number" placeholder="Track Number" required="true" class="element-field"/>
                    <!-- Remove button for existing songs -->
                    <button type="button" class="remove-button" onclick="removeSong(${status.index})">Remove Song</button>
                </div>
            </c:forEach>
        </div>
        <div>
            <button type="button" id="addSongButton" onclick="addSong()">+ Add Song</button>
        </div>

        <!-- Submit Button -->
        <br><br>
        <div>
            <button type="submit">Submit Album</button>
        </div>
    </form:form>

    <script>
        var songIndex = 0;
        var songCount = 0;
        var maxSongs = 3;
        function addSong() {
            var container = document.getElementById("SongContainer");
            container.setAttribute("class", "element-table");

            var newSongDiv = document.createElement("div");
            newSongDiv.setAttribute("class", "element-row");

            var titleInput = document.createElement("input");
            titleInput.setAttribute("type", "text");
            titleInput.setAttribute("name", "songs[" + songIndex + "].title");
            titleInput.setAttribute("placeholder", "Title");
            titleInput.setAttribute("required", "true");
            titleInput.setAttribute("class", "element-field");
            newSongDiv.appendChild(titleInput);

            var durationInput = document.createElement("input");
            durationInput.setAttribute("type", "text");
            durationInput.setAttribute("name", "songs[" + songIndex + "].duration");
            durationInput.setAttribute("placeholder", "Duration in MM:SS");
            durationInput.setAttribute("required", "true");
            durationInput.setAttribute("class", "element-field");
            newSongDiv.appendChild(durationInput);

            var trackNumberInput = document.createElement("input");
            trackNumberInput.setAttribute("type", "number");
            trackNumberInput.setAttribute("name", "songs[" + songIndex + "].trackNumber");
            trackNumberInput.setAttribute("required", "true");
            trackNumberInput.setAttribute("class", "element-field");
            newSongDiv.appendChild(trackNumberInput);

            var removeButton = document.createElement("button");
            removeButton.textContent = "Remove Song";
            removeButton.setAttribute("type", "button");
            removeButton.setAttribute("class", "remove-button");
            removeButton.onclick = function () {
                container.removeChild(newSongDiv);
            };
            newSongDiv.appendChild(removeButton);

            container.appendChild(newSongDiv);

            songIndex++;
            songCount++;

            // Update the button's enabled/disabled state
            //toggleAddButton(albumCount);
        }

        function removeSong(index) {
            var container = document.getElementById("SongContainer");
            var songDiv = document.getElementById("song-" + index);

            if (songDiv) {
                container.removeChild(songDiv);
                songCount--;  // Decrease the count when an album is removed
            }

            // Update the button's enabled/disabled state
            //toggleAddButton(albumCount);
        }

        /* Disables but does not enable the button

        function toggleAddButton(count) {
            var addSongButton = document.getElementById("addSongButton");

            if (count >= maxSongs) {
                addSongButton.disabled = true;  // Disable the button when the max is reached
            } else {
                addSongButton.disabled = false; // Re-enable the button if the count is below the max
            }
        }
        */

        // Previews Image inserted
        function previewImage(event) {
            const file = event.target.files[0];
            const preview = document.getElementById('imagePreview');

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