<%--
  Created by IntelliJ IDEA.
  User: manuader
  Date: 22/08/2024
  Time: 4:45 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Artist Page</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #121212;
            color: #ffffff;
            margin: 0;
            padding: 0;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        header {
            display: flex;
            align-items: center;
            margin-bottom: 30px;
        }
        .artist-image {
            width: 200px;
            height: 200px;
            border-radius: 50%;
            object-fit: cover;
            margin-right: 20px;
        }
        h1 {
            font-size: 48px;
            margin: 0;
        }
        h2 {
            font-size: 24px;
            color: #1db954;
            margin-top: 40px;
        }
        .carousel {
            display: flex;
            overflow-x: auto;
            gap: 20px;
            padding: 20px 0;
        }
        .album {
            flex: 0 0 auto;
            width: 200px;
            text-align: center;
        }
        .album img {
            width: 100%;
            height: 200px;
            object-fit: cover;
            border-radius: 8px;
        }
        .album p {
            margin: 10px 0 0;
        }
        .song-list {
            list-style-type: none;
            padding: 0;
        }
        .song-list li {
            padding: 10px 0;
            border-bottom: 1px solid #282828;
        }
        .song-list li:last-child {
            border-bottom: none;
        }
    </style>
</head>
<body>
<div class="container">
    <header>
        <img src="" alt="Artist Name" class="artist-image">
        <h1><c:out value="${artist.name}"/></h1>
        <p><c:out value="${artist.bio}"/></p>
    </header>

    <h2>Albums</h2>
    <div class="carousel">
        <c:forEach var="album" items="${albums}" varStatus="status">
            <div class="album">
                <img src="/api/placeholder/200/200" alt="Album ${status.index + 1}">
                <p><c:out value="${album.title}"/></p>
            </div>
        </c:forEach>
    </div>

    <h2>Popular Songs</h2>
    <ul class="song-list">
        <c:forEach var="song" items="${songs}" varStatus="status">
            <li>${status.index + 1} - <c:out value="${song.title}"/></li>
        </c:forEach>
    </ul>
</div>
</body>
</html>