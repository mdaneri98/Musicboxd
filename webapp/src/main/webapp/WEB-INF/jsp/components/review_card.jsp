<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Review Card</title>
    <style>
        :root {
            --background-base: #121212;
            --text-base: #fff;
            --text-subdued: #a7a7a7;
            --star-color: #ffb400;
        }

        body {
            font-family: Arial, sans-serif;
            background-color: var(--background-base);
            color: var(--text-base);
            margin: 0;
            padding: 20px;
        }

        .review-card {
            background-color: #181818;
            border-radius: 8px;
            padding: 16px;
            max-width: 600px;
            margin: 0 auto;
        }

        .album-info {
            display: flex;
            align-items: center;
            margin-bottom: 16px;
        }

        .album-cover {
            width: 80px;
            height: 80px;
            border-radius: 4px;
            margin-right: 16px;
            object-fit: cover;
        }

        .album-details h2, .album-details p {
            margin: 0;
        }

        .album-details p {
            color: var(--text-subdued);
        }

        .review-title {
            font-size: 1.2em;
            font-weight: bold;
            margin-bottom: 8px;
        }

        .review-rating {
            color: var(--star-color);
            font-size: 1.5em;
            margin-bottom: 8px;
        }

        .review-content {
            margin-bottom: 16px;
            line-height: 1.4;
        }

        .read-more {
            background-color: #282828;
            color: var(--text-base);
            border: none;
            padding: 8px 16px;
            border-radius: 4px;
            cursor: pointer;
            width: 100%;
        }

        .user-info {
            display: flex;
            align-items: center;
            margin-top: 16px;
            text-decoration: none;
            color: inherit;
        }

        .user-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            margin-right: 12px;
            object-fit: cover;
        }

        .user-name {
            font-weight: bold;
        }

        .review-actions {
            display: flex;
            justify-content: space-between;
            margin-top: 8px;
        }

        .review-actions button {
            background: none;
            border: none;
            color: var(--text-subdued);
            cursor: pointer;
        }
        .star-rating {
            unicode-bidi: bidi-override;
            color: #ccc;
            font-size: 1.5em;
            position: relative;
            margin: 0;
            padding: 0;
        }
        .star-rating .fill {
            color: var(--star-color);
            padding: 0;
            position: absolute;
            z-index: 1;
            display: block;
            top: 0;
            left: 0;
            overflow: hidden;
        }
        .star-rating .base {
            z-index: 0;
            padding: 0;
        }
    </style>
</head>
<body>
<div class="review-card">
    <c:url var="itemUrl" value="${param.item_url}" />
    <c:url var="artistUrl" value="${param.artist_url}" />
    <div class="album-info">
        <a href="${itemUrl}">
        <c:url var="reviewImgUrl" value="/images/${param.item_img_id}" />
        <img src="${reviewImgUrl}" alt="${param.item_name} Cover" class="album-cover">
        <div class="album-details">
            <h2><c:out value="${param.item_name}"/></h2>
            <a href="${artistUrl}">
                <p><c:out value="${param.item_type}"/></p>
            </a>
        </div>
        </a>
    </div>
    <div class="star-rating">
        <div class="fill" style="width: ${param.rating * 20}%;">
            <span><c:forEach var="i" begin="1" end="${param.rating}">&#9733;</c:forEach></span>
        </div>
        <div class="base">
            <span>&#9733;&#9733;&#9733;&#9733;&#9733;</span>
        </div>
    </div>
    <div class="review-title"><c:out value="${param.title}"/></div>
    <div class="review-content">
        <c:choose>
            <c:when test="${fn:length(param.review_content) > 200}">
                <c:out value="${fn:substring(param.review_content, 0, 200)}"/>...
                <button class="read-more">Read more...</button>
            </c:when>
            <c:otherwise>
                <c:out value="${param.review_content}"/>
            </c:otherwise>
        </c:choose>
    </div>
    <c:url var="userUrl" value="/user/${param.user_id}" />
    <a href="${userUrl}" class="user-info">
        <c:url var="userImgUrl" value="/images/${param.user_img_id}" />
        <img src="${userImgUrl}" alt="${param.user_name} Avatar" class="user-avatar">
        <div>
            <div class="user-name"><c:out value="${param.user_name}"/></div>
        </div>
    </a>
    <div class="review-actions">
        <button onclick="likeReview(${param.review_id})"><c:out value="${param.likes}"/> &#9825; Like</button>
        <button onclick="shareReview(${param.review_id})">&#10150; Share</button>
    </div>
</div>

<script>
    function likeReview(reviewId) {
        // Implementar lógica de like
        console.log('Like review:', reviewId);
    }

    function shareReview(reviewId) {
        // Implementar lógica de compartir
        console.log('Share review:', reviewId);
    }
</script>
</body>
</html>