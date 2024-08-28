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
        <img src="data:image/webp;base64,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" alt="Artist Name" class="artist-image">
        <h1><c:out value="${username}"/></h1>
    </header>

    <h2>Albums</h2>
    <div class="carousel">
        <div class="album">
            <img src="/api/placeholder/200/200" alt="Album 1">
            <p>Album 1</p>
        </div>
        <div class="album">
            <img src="/api/placeholder/200/200" alt="Album 2">
            <p>Album 2</p>
        </div>
        <div class="album">
            <img src="/api/placeholder/200/200" alt="Album 3">
            <p>Album 3</p>
        </div>
        <div class="album">
            <img src="/api/placeholder/200/200" alt="Album 4">
            <p>Album 4</p>
        </div>
    </div>

    <h2>Popular Songs</h2>
    <ul class="song-list">
        <li>Song 1</li>
        <li>Song 2</li>
        <li>Song 3</li>
        <li>Song 4</li>
        <li>Song 5</li>
    </ul>
</div>
</body>
</html>