<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="review-container">
    <div class="review-header">
        <c:url var="itemUrl" value="${param.item_url}" />
        <c:url var="artistUrl" value="${param.artist_url}" />
        <div>
            <a href="${itemUrl}">
                <c:url var="reviewImgUrl" value="/images/${param.item_img_id}" />
                <img src="${reviewImgUrl}" alt="${param.item_name} Cover" class="item-cover">
            </a>
        </div>
        <div>
            <h2><c:out value="${param.item_name}"/></h2>
        </div>
        <div>
            <p><c:out value="${param.item_type}"/></p>
        </div>
    </div>
    <hr style="border: 1px solid #a6a6a6;">
    <div class="review-content">
        <div class="review-title"><c:out value="${param.title}"/></div>
        <div class="review-content">
            <c:out value="${param.review_content}"/>
        </div>
    </div>
    <hr style="border: 1px solid #a6a6a6;">
    <div class="review-footer">
        <div>
            <c:url var="userUrl" value="/user/${param.user_id}" />
            <a href="${userUrl}">
                <c:url var="userImgUrl" value="/images/${param.user_img_id}" />
                <img src="${userImgUrl}" alt="${param.user_name} Avatar" class="user-avatar">
                <div class="user-name"><c:out value="${param.user_name}"/></div>
            </a>
        </div>
        <div class="star-rating">
            <div class="fill" style="width: ${param.rating * 20}%;">
                <span><c:forEach var="i" begin="1" end="${param.rating}">&#9733;</c:forEach></span>
            </div>
        </div>
        <div class="review-actions">
            <c:url var="likeReviewLink" value="/review/like/${param.review_id}" />
            <c:url var="shareReviewLink" value="/review/share/${param.review_id}" />
            <a href="${likeReviewLink}"><c:out value="${param.likes}"/> &#9825; Like</a>
            <a href="${shareReviewLink}">&#10150; Share</a>
        </div>
    </div>
</div>
