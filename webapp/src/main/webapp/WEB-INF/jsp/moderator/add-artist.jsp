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

    <c:url var="css" value="/static/css/artist_review.css" />
    <link rel="stylesheet" href="${css}">

</head>
<body>
<div>
    <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
        <jsp:param name="loggedUserImgId" value="${loggedUser.imgId}"/>
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
                <form:input path="name" required="true" />
            </label>
        </div>
        <div>
            <label>Bio:
                <form:errors path="bio" cssClass="error" element="p" cssStyle="color:red;"/>
                <form:textarea path="bio" rows="5" required="true" />
            </label>
        </div>
        <div>
            <label>Image:
                <form:errors path="artistImage" cssClass="error" element="p" cssStyle="color:red;"/>
                <form:input path="artistImage" type="file" accept=".jpg,.jpeg,.png" required="true"/>
            </label>
        </div>

        <!-- Album List Section -->
        <h2>Albums</h2>
        <div id="AlbumContainer">
            <c:forEach items="${modArtistForm.albums}" var="album" varStatus="status">
                <div id="album-${status.index}" class="album-entry">
                    <label>Album Title:</label>
                    <form:input path="albums[${status.index}].title" type="text"/>
                    <label>Album Genre:</label>
                    <form:input path="albums[${status.index}].genre" type="text"/>
                    <label>Album Image:</label>
                    <form:input path="albums[${status.index}].albumImage" type="file" accept=".jpg,.jpeg,.png"/>

                    <!-- Remove button for existing artist -->
                    <button type="button" class="remove-album" onclick="removeAlbum(${status.index})">Remove Album</button>
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
        let albumIndex = 0;
        let albumCount = 0;
        let maxAlbums = 3;
        function addAlbum() {
            let container = document.getElementById("AlbumContainer");
            let newAlbumDiv = document.createElement("div");
            newAlbumDiv.setAttribute("class", "album-entry");

            let nameLabel = document.createElement("label");
            nameLabel.textContent = "Album Title: ";
            newAlbumDiv.appendChild(nameLabel);

            let nameInput = document.createElement("input");
            nameInput.setAttribute("type", "text");
            nameInput.setAttribute("name", "albums[" + albumIndex + "].title");
            newAlbumDiv.appendChild(nameInput);

            let bioLabel = document.createElement("label");
            bioLabel.textContent = "Album Genre: ";
            newAlbumDiv.appendChild(bioLabel);

            let bioInput = document.createElement("input");
            bioInput.setAttribute("type", "text");
            bioInput.setAttribute("name", "albums[" + albumIndex + "].genre");
            newAlbumDiv.appendChild(bioInput);

            let ImageLabel = document.createElement("label");
            ImageLabel.textContent = "Album Image: ";
            newAlbumDiv.appendChild(ImageLabel);

            let ImageInput = document.createElement("input");
            ImageInput.setAttribute("type", "file");
            ImageInput.setAttribute("name", "albums[" + albumIndex + "].albumImage");
            ImageInput.setAttribute("accept", ".jpg,.jpeg,.png");
            newAlbumDiv.appendChild(ImageInput);

            // Add Remove Button
            let removeButton = document.createElement("button");
            removeButton.textContent = "Remove Album";
            removeButton.setAttribute("type", "button");
            removeButton.onclick = function() {
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
            let container = document.getElementById("AlbumContainer");
            let albumDiv = document.getElementById("album-" + index);

            if (albumDiv) {
                container.removeChild(albumDiv);
                albumCount--;  // Decrease the count when an album is removed
            }

            // Update the button's enabled/disabled state
            //toggleAddButton(albumCount);
        }

        /* Disables but does not enable the button

        function toggleAddButton(count) {
            let addAlbumButton = document.getElementById("addAlbumButton");

            if (count >= maxAlbums) {
                addAlbumButton.disabled = true;  // Disable the button when the max is reached
            } else {
                addAlbumButton.disabled = false; // Re-enable the button if the count is below the max
            }
        }
        */

    </script>
    <style>
        .album-entry button.remove-album {
            background-color: #e74c3c;
            color: white;
            border: none;
            padding: 5px 10px;
            cursor: pointer;
        }

        .album-entry button.remove-album:hover {
            background-color: #c0392b;
        }
    </style>
</div>
</body>
</html>


