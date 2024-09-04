<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: manuader
  Date: 01/09/2024
  Time: 1:05 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
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

        .song-box {
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

        .song-box:hover {
            background-color: var(--background-press);
            transform: translateY(-5px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
        }

        .song-image {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            object-fit: cover;
            margin-right: 24px;
        }

        .song-info {
            flex-grow: 1;
        }

        .song-title {
            font-size: 24px;
            font-weight: 700;
            margin-bottom: 8px;
        }

        .song-duration {
            font-size: 14px;
            color: var(--text-subdued);
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Review Song</h1>

    <a href="/webapp_war/song/${song.id}" class="song-box">
        <img src="${song.imgId}" alt="${song.title}" class="song-image">
        <div class="song-info">
            <h2 class="song-title">${song.title}</h2>
            <p class="song-duration">${song.duration}</p>
        </div>
    </a>

    <c:import url="../components/review_form.jsp"/>
</div>
</body>
</html>
