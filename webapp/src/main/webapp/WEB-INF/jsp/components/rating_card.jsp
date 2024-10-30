<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="rating-card">
    <div class="rating-stats">
        <div class="rating-stat-item">
            <div class="rating-value">${param.totalRatings}</div>
            <div class="rating-label">
                <spring:message code="label.total.ratings" />
            </div>
        </div>
        <div class="rating-stat-item">
            <div class="rating-value">
                <span class="star filled">&#9733;</span>
                <fmt:formatNumber value="${param.averageRating}" type="number" maxFractionDigits="2"/>
                <span class="rating-max">/ 5</span>
            </div>
            <div class="rating-label">
                <spring:message code="label.avg.rating" />
            </div>
        </div>
        <div class="rating-stat-item">
            <div class="rating-value">
                <c:if test="${param.reviewed}">
                    <span class="star filled">&#9733;</span>
                </c:if>
                <c:if test="${!param.reviewed}">
                    <span class="star">&#9733;</span>
                </c:if>
                ${param.userRating}<span class="rating-max">/ 5</span>
            </div>
            <div class="rating-label">
                <spring:message code="label.your.rating" />
            </div>
        </div>
    </div>

    <div class="rating-actions">
        <c:if test="${!param.reviewed}">
            <c:url var="reviewUrl" value="/${param.entityType}/${param.entityId}/reviews"/>
            <a href="${reviewUrl}" class="btn btn-primary btn-block">
                <i class="fas fa-star"></i>
                <spring:message code="label.rate.this"/>
            </a>
        </c:if>
        <c:if test="${param.reviewed}">
            <c:url var="editReviewUrl" value="/${param.entityType}/${param.entityId}/edit-review"/>
            <a href="${editReviewUrl}" class="btn btn-secondary btn-block">
                <i class="fas fa-edit"></i>
                <spring:message code="label.edit.your.review" />
            </a>
        </c:if>
    </div>
</div>