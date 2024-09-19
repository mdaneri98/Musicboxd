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

        .review-card {
            background-color: #181818;
            border-radius: 10px;
            padding: 16px;
            max-width: 800px;
            margin: 0 auto;
            margin-bottom: 10px;
        }

        .review-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 16px;
        }

        .item-info {
            display: flex;
            align-items: center;
            flex: 1;
        }

        .item-cover {
            width: 60px;
            height: 60px;
            border-radius: 4px;
            margin-right: 12px;
            object-fit: cover;
        }

        .item-details {
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        .item-details h2 {
            margin: 0;
            font-size: 1.1em;
            line-height: 1.2;
        }

        .item-details p {
            margin: 0;
            color: var(--text-subdued);
            font-size: 0.9em;
        }

        .user-info {
            display: flex;
            align-items: center;
            text-decoration: none;
            color: inherit;
        }

        .user-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            margin-right: 8px;
            object-fit: cover;
        }

        .user-name {
            font-weight: bold;
            font-size: 0.9em;
        }
    </style>
</head>
<body>
<div class="review-card">
    <div class="review-header">
        <c:url var="itemUrl" value="${param.item_url}" />
        <c:url var="artistUrl" value="${param.artist_url}" />
        <div class="item-info">
            <a href="${itemUrl}">
                <c:url var="reviewImgUrl" value="/images/${param.item_img_id}" />
                <img src="${reviewImgUrl}" alt="${param.item_name} Cover" class="item-cover">
                <div class="item-details">
                    <h2><c:out value="${param.item_name}"/></h2>
                    <p><c:out value="${param.item_type}"/></p>
                </div>
            </a>
        </div>
        <c:url var="userUrl" value="/user/${param.user_id}" />
        <a href="${userUrl}" class="user-info">
            <c:url var="userImgUrl" value="/images/${param.user_img_id}" />
            <img src="${userImgUrl}" alt="${param.user_name} Avatar" class="user-avatar">
            <div class="user-name"><c:out value="${param.user_name}"/></div>
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
    <div class="review-actions">
        <c:url var="likeReviewLink" value="/review/like/${param.review_id}" />
        <c:url var="shareReviewLink" value="/review/share/${param.review_id}" />
        <a href="${likeReviewLink}"><c:out value="${param.likes}"/> &#9825; Like</a>
        <a href="${shareReviewLink}">&#10150; Share</a>
    </div>
</div>
</body>
</html>