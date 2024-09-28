<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style>
    .rating-component {
        background-color: #121212;
        border-radius: 8px;
        padding: 20px;
        width: 300px;
        font-family: 'Circular Std', Arial, sans-serif;
        color: #fff;
    }
    .rating-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        border-bottom: 1px solid #282828;
        padding-bottom: 15px;
    }
    .rating-item {
        text-align: center;
    }
    .rating-number {
        font-size: 24px;
        font-weight: bold;
        margin-bottom: 5px;
    }
    .rating-text {
        font-size: 12px;
        color: #b3b3b3;
    }
    .rating-star {
        color: #ffce00;
        font-size: 20px;
        margin-right: 5px;
    }
    .rating-fraction {
        font-size: 16px;
        color: #b3b3b3;
    }
    .edit-rating-button {
        width: 100%;
        padding: 14px 0;
        border: none;
        border-radius: 500px;
        color: #000;
        font-size: 14px;
        font-weight: bold;
        cursor: pointer;
        transition: background-color 0.3s ease;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    .edit-rating-button:hover {
        background-color: #ffd633;
    }
    .edit-icon {
        margin-right: 8px;
    }
</style>

<div class="rating-component">
    <div class="rating-header">
        <div class="rating-item">
            <div class="rating-number">${param.totalRatings}</div>
            <div class="rating-text">Total ratings</div>
        </div>
        <div class="rating-item">
            <div class="rating-number">
                <span class="rating-star">&#9733;</span>${param.averageRating}<span class="rating-fraction">/ 5</span>
            </div>
            <div class="rating-text">Average rating</div>
        </div>
        <div class="rating-item">
            <div class="rating-number">
                <c:if test="${param.reviewed}">
                <span class="rating-star">&#9733;</span>${param.userRating}<span class="rating-fraction">/ 5</span>
                </c:if>
                <c:if test="${!param.reviewed}">
                    <span class="rating-star" style="fill: #5c5c5c">&#9733;</span>${param.userRating}<span class="rating-fraction">/ 5</span>
                </c:if>
            </div>
            <div class="rating-text">Your rating</div>
        </div>
    </div>
    <c:if test="${!param.reviewed}">
        <c:url var="reviewUrl" value="/${param.entityType}/${param.entityId}/reviews"/>
        <a href="${reviewUrl}">
    <button class="edit-rating-button">
        <svg class="edit-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
        </svg>
        Rate this <c:out value="${param.entityType}"/>
    </button>
        </a>
    </c:if>
    <c:if test="${param.reviewed}">
        <c:url var="editReviewUrl" value="/${param.entityType}/${param.entityId}/edit-reviews"/>
    <a href="${editReviewUrl}">
        <button class="edit-rating-button" style="background-color: #b99a1d">
            <svg class="edit-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
            </svg>
            Edit your review
        </button>
    </a>
    </c:if>
</div>