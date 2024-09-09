<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${song.title} - Song Page</title>
  <style>
    :root {
      --background-base: #121212;
      --background-highlight: #1a1a1a;
      --background-press: #000;
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
      --essential-bright-accent: #1ed760;
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

    .song-header {
      display: flex;
      align-items: flex-end;
      margin-bottom: 40px;
      padding: 60px 0;
      background: linear-gradient(transparent 0,rgba(0,0,0,.5) 100%);
    }

    .song-image {
      width: 232px;
      height: 232px;
      object-fit: cover;
      margin-right: 24px;
      box-shadow: 0 4px 60px rgba(0,0,0,.5);
    }

    .song-info {
      flex-grow: 1;
    }

    .song-type {
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

    .buttons-container {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      margin-top: 24px;
    }

    .button {
      display: inline-flex;
      align-items: center;
      font-size: 14px;
      font-weight: 700;
      text-decoration: none;
      padding: 6px 12px;
      border-radius: 500px;
      margin-right: 16px;
      transition: background-color 0.3s ease, transform 0.3s ease;
    }

    .artist-button {
      background-color: var(--background-tinted-highlight);
      color: var(--text-base);
      padding-right: 16px;
    }

    .artist-button:hover {
      background-color: var(--background-tinted-press);
      transform: scale(1.04);
    }

    .artist-image {
      width: 28px;
      height: 28px;
      border-radius: 50%;
      object-fit: cover;
      margin-right: 8px;
    }

    .review-button {
      background-color: var(--essential-bright-accent);
      color: var(--background-base);
    }

    .review-button:hover {
      background-color: #1fdf64;
      transform: scale(1.04);
    }

    .album-card {
      background-color: var(--background-tinted-highlight);
      color: var(--text-base);
      padding: 12px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      max-width: 250px;
      margin-right: 20px;
    }

    .album-card:hover {
      background-color: var(--background-tinted-press);
      transform: scale(1.04);
    }

    .album-image {
      width: 48px;
      height: 48px;
      border-radius: 4px;
      object-fit: cover;
      margin-right: 12px;
    }

    .album-info {
      display: flex;
      flex-direction: column;
    }

    .album-name {
      font-size: 16px;
      font-weight: 700;
    }

    .album-type {
      font-size: 12px;
      color: var(--text-subdued);
    }

    .song-description {
      background-color: var(--background-elevated-base);
      border-radius: 8px;
      padding: 24px;
      margin-top: 24px;
      font-size: 16px;
      color: var(--text-subdued);
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
    <div class="song-header">
      <c:url var="songImgUrl" value="/images/${album.imgId}"/>
      <img src="${songImgUrl}" alt="${song.title}" class="song-image">
      <div class="song-info">
        <p class="song-type">Song</p>
        <h1><c:out value="${song.title}"/></h1>
        <div class="buttons-container button-group">
          <c:forEach var="artist" items="${artists}">
            <c:url var="artistUrl" value="/artist/${artist.id}"/>
            <a href="${artistUrl}" class="button artist-button">
              <c:url var="artistImgUrl" value="/images/${artist.imgId}"/>
              <img src="${artistImgUrl}" alt="${artist.name}" class="artist-image">
              <span><c:out value="${artist.name}"/></span>
            </a>
          </c:forEach>
        </div>
        <div class="buttons-container">
          <c:url var="songUrl" value="/album/${song.albumId}"/>
          <a href="${songUrl}" class="album-card">
            <c:url var="albumImgUrl" value="/images/${album.imgId}"/>
            <img src="${albumImgUrl}" alt="${album.title}" class="album-image">
            <div class="album-info">
              <span class="album-name"><c:out value="${album.title}"/></span>
              <span class="album-type">Album</span>
            </div>
          </a>
          <c:url var="songReviewUrl" value="/review/song/${song.id}"/>
          <a href="${songReviewUrl}" class="button review-button">Make a review</a>
        </div>
      </div>
    </div>

    <div class="song-description">
      <p><c:out value="${song.duration}"/></p>
    </div>
  </div>
</body>
</html>