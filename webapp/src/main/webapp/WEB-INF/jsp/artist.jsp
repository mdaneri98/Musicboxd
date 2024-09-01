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
        :root {
            --background-base: #121212;
            --background-highlight: #1a1a1a;
            --background-press: #2c2c2c;
            --background-elevated-base: #242424;
            --background-elevated-highlight: #2a2a2a;
            --background-tinted-base: hsla(0,0%,100%,.07);
            --background-tinted-highlight: hsla(0,0%,100%,.1);
            --background-tinted-press: hsla(0,0%,100%,.04);
            --text-base: #fff;
            --text-subdued: #a7a7a7;
            --text-bright-accent: #1ed760;
            --essential-base: #fff;
            --essential-subdued: #727272;
            --essential-bright-accent: #20a34f;
            --decorative-base: #fff;
            --decorative-subdued: #292929;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Circular Std', Arial, sans-serif;
            background-color: var(--background-base);
            color: var(--text-base);
            line-height: 1.6;
        }

        .container {
            max-width: 1400px;
            margin: 0 auto;
            padding: 24px;
        }

        header {
            display: flex;
            align-items: flex-end;
            margin-bottom: 40px;
            padding: 60px 0;
            background: linear-gradient(transparent 0,rgba(0,0,0,.5) 100%);
        }

        .artist-image {
            width: 232px;
            height: 232px;
            border-radius: 50%;
            object-fit: cover;
            margin-right: 24px;
            box-shadow: 0 4px 60px rgba(0,0,0,.5);
        }

        .artist-info {
            flex-grow: 1;
        }

        .artist-type {
            font-size: 14px;
            font-weight: 700;
            text-transform: uppercase;
            margin-bottom: 8px;
        }

        h1 {
            font-size: 96px;
            line-height: 96px;
            font-weight: 900;
            letter-spacing: -0.04em;
            margin-bottom: 16px;
        }

        .artist-bio {
            font-size: 14px;
            color: var(--text-subdued);
            max-width: 800px;
        }

        h2 {
            font-size: 24px;
            font-weight: 700;
            margin: 32px 0 16px;
        }

        .carousel {
            display: flex;
            overflow-x: auto;
            gap: 24px;
            scroll-behavior: smooth;
        }

        .album {
            background-color: var(--background-elevated-base);
            width: 200px;
            height: auto;
            border-radius: 8px;
            padding: 16px;
            transition: background-color 0.3s ease;
        }

        .album:hover {
            background-color: var(--background-elevated-highlight);
        }

        .album img {
            width: 100%;
            aspect-ratio: 1;
            object-fit: cover;
            border-radius: 8px;
            margin-bottom: 8px;
        }

        .album p {
            font-size: 16px;
            font-weight: 700;
            color: var(--text-base);
        }

        .song-list {
            list-style-type: none;
        }

        .song-list li {
            display: flex;
            align-items: center;
            padding: 8px 16px;
            border-radius: 4px;
            transition: background-color 0.3s ease;
        }

        .song-list li:hover {
            background-color: var(--background-tinted-highlight);
        }

        .song-number {
            color: var(--text-subdued);
            margin-right: 16px;
            font-variant-numeric: tabular-nums;
            min-width: 16px;
            text-align: right;
        }

        .song-title {
            flex-grow: 1;
            font-size: 16px;
        }

        button {
            display: inline-block;
            background-color: var(--essential-bright-accent);
            color: var(--background-base);
            font-size: 14px;
            font-weight: 700;
            text-decoration: none;
            padding: 14px 32px;
            border-radius: 500px;
            margin-top: 24px;
            transition: background-color 0.3s ease, transform 0.3s ease;
        }

        button:hover {
            background-color: #1fdf64;
            transform: scale(1.04);
        }

        a {
            text-decoration: none;
            color: inherit;
            background-color: transparent;
        }
    </style>
</head>
<body>
<div class="container">
    <header>
        <img src="" alt="Artist Name" class="artist-image">
        <div class="artist-info">
            <p class="artist-type">Artist</p>
            <h1><c:out value="${artist.name}"/></h1>
            <p class="artist-bio"><c:out value="${artist.bio}"/></p>
            <a href="/webapp_war/review/artist/${artist.id}">
                <button>Make a review</button>
            </a>
        </div>
    </header>

    <h2>Albums</h2>
    <div class="carousel">
        <c:forEach var="album" items="${albums}" varStatus="status">
            <a href="/webapp_war/album/${album.id}">
                <div class="album">
                    <img src="https://upload.wikimedia.org/wikipedia/en/thumb/4/42/Beatles_-_Abbey_Road.jpg/220px-Beatles_-_Abbey_Road.jpg" alt="Album ${status.index + 1}">
                    <p><c:out value="${album.title}"/></p>
                </div>
            </a>
        </c:forEach>
    </div>

    <h2>Popular Songs</h2>
    <ul class="song-list">
        <c:forEach var="song" items="${songs}" varStatus="status">
            <a href="/webapp_war/song/${song.id}">
                <li>
                    <span class="song-number">${status.index + 1}</span>
                    <span class="song-title"><c:out value="${song.title}"/></span>
                </li>
            </a>
        </c:forEach>
    </ul>
</div>
</body>
</html>