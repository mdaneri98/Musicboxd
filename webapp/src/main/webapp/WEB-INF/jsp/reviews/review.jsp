<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Review Artist</title>
  <style>
    :root {
      --background-base: #000000;
      --background-highlight: #1a1a1a;
      --background-press: #2c2c2c;
      --text-base: #fff;
      --text-subdued: #a7a7a7;
      --essential-bright-accent: #20a34f;
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
      line-height: 1.5;
    }

    .container {
      max-width: 800px;
      margin: 0 auto;
      padding: 40px 20px;
    }

    h1 {
      font-size: 32px;
      font-weight: 700;
      margin-bottom: 24px;
      text-align: center;
    }

    .artist-box {
      background-color: var(--background-highlight);
      border-radius: 8px;
      padding: 24px;
      margin-bottom: 24px;
      display: flex;
      align-items: center;
      cursor: pointer;
      transition: background-color 0.3s ease, transform 0.3s ease;
      text-decoration: none;
      color: var(--text-base);
    }

    .artist-box:hover {
      background-color: var(--background-press);
      transform: translateY(-5px);
      box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
    }

    .artist-image {
      width: 120px;
      height: 120px;
      border-radius: 50%;
      object-fit: cover;
      margin-right: 24px;
    }

    .artist-info {
      flex-grow: 1;
    }

    .artist-name {
      font-size: 24px;
      font-weight: 700;
      margin-bottom: 8px;
    }

    .artist-bio {
      font-size: 14px;
      color: var(--text-subdued);
    }

    form {
      background-color: var(--background-highlight);
      padding: 24px;
      border-radius: 8px;
    }

    label {
      display: block;
      margin-bottom: 8px;
      font-weight: 600;
    }

    input[type="text"],
    textarea {
      width: 100%;
      padding: 12px;
      margin-bottom: 16px;
      background-color: var(--background-press);
      border: none;
      border-radius: 4px;
      color: var(--text-base);
      font-size: 16px;
    }

    textarea {
      height: 120px;
      resize: vertical;
    }
    .star-rating {
      display: flex;
      flex-direction: row-reverse;
      justify-content: flex-end;
      margin-bottom: 16px;
    }

    .star-rating input {
      display: none;
    }

    .star-rating label {
      cursor: pointer;
      font-size: 30px;
      color: var(--text-subdued);
      transition: color 0.2s ease-in-out;
    }

    .star-rating label:hover,
    .star-rating label:hover ~ label,
    .star-rating input:checked ~ label {
      color: var(--essential-bright-accent);
    }

    /* Nuevo estilo para las estrellas */
    .star-rating label:before {
      content: "\2606"; /* Unicode para estrella vacía */
      padding: 5px;
    }

    .star-rating input:checked ~ label:before {
      content: "\2605"; /* Unicode para estrella llena */
    }

    .star-rating label:hover,
    .star-rating label:hover ~ label,
    .star-rating input:checked ~ label {
      color: var(--essential-bright-accent);
    }

    button {
      display: block;
      width: 100%;
      padding: 14px;
      background-color: var(--essential-bright-accent);
      color: var(--background-base);
      border: none;
      border-radius: 500px;
      font-size: 16px;
      font-weight: 700;
      cursor: pointer;
      transition: background-color 0.4s ease-in-out;
    }

    button:hover {
      background-color: #30c668;
      transform: scale(1.03);
    }
  </style>
</head>
<body>
  <form action="/submit-review" method="post">
    <input type="hidden" name="artistId" value="${artist.id}">

    <label for="title">Review Title</label>
    <input type="text" id="title" name="title" required>

    <label for="description">Review Description</label>
    <textarea id="description" name="description" required></textarea>

    <label>Rating</label>
    <div class="star-rating">
      <input type="radio" id="star5" name="rating" value="5" required>
      <label for="star5" title="5 stars"></label>
      <input type="radio" id="star4" name="rating" value="4">
      <label for="star4" title="4 stars"></label>
      <input type="radio" id="star3" name="rating" value="3">
      <label for="star3" title="3 stars"></label>
      <input type="radio" id="star2" name="rating" value="2">
      <label for="star2" title="2 stars"></label>
      <input type="radio" id="star1" name="rating" value="1">
      <label for="star1" title="1 star"></label>
    </div>

    <button type="submit">Submit Review</button>
  </form>
</body>
</html>