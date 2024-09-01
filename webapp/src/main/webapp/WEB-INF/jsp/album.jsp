<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${album.title} - Album Page</title>
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

    .album-header {
      display: flex;
      align-items: flex-end;
      margin-bottom: 40px;
      padding: 60px 0;
      background: linear-gradient(transparent 0,rgba(0,0,0,.5) 100%);
    }

    .album-image {
      width: 232px;
      height: 232px;
      object-fit: cover;
      margin-right: 24px;
      box-shadow: 0 4px 60px rgba(0,0,0,.5);
    }

    .album-info {
      flex-grow: 1;
    }

    .album-type {
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

    .song-list {
      list-style-type: none;
      background-color: var(--background-elevated-base);
      border-radius: 8px;
      padding: 16px;
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
      color: var(--text-base);
      text-decoration: none;
    }

    .song-title:hover {
      text-decoration: underline;
    }

    .button-group {
      display: flex;
      align-items: center;
    }

    .button-group .button {
      margin-right: 16px;
    }

    .button-group .button:last-child {
      margin-right: 0;
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
      <div class="album-header">
        <img src="/api/placeholder/232/232" alt="${album.title}" class="album-image">
        <div class="album-info">
          <p class="album-type">Album</p>
          <h1><c:out value="${album.title}"/></h1>
          <div class="button-group">
            <a href="/webapp_war/artist/${artist.id}" class="button artist-button">
              <img src="/api/placeholder/28/28" alt="${artist.name}" class="artist-image">
              <span><c:out value="${artist.name}"/></span>
            </a>
            <a href="/webapp_war/review/album/${album.id}" class="button review-button">Make a review</a>
          </div>
        </div>
      </div>

      <ul class="song-list">
        <c:forEach var="song" items="${songs}" varStatus="status">
          <li>
            <span class="song-number">${status.index + 1}</span>
            <a href="/webapp_war/song/${song.id}" class="song-title"><c:out value="${song.title}"/></a>
          </li>
        </c:forEach>
      </ul>
    </div>
  </body>
</html>