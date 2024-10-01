<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:url var="css" value="/static/css/user_card.css" />
<link rel="stylesheet" href="${css}">

<c:url var="profileUrl" value="/user/${param.id}"/>
<a href="${profileUrl}">
<div class="user-card">
    <div class="user-card-header">
        <c:url var="userImgUrl" value="/images/${param.imgId}"/>
        <img src="${userImgUrl}" alt="${param.username}" class="user-card-image">
        <div class="user-card-info">
            <h2 class="user-card-username"><c:out value="${param.username}"/></h2>
            <p class="user-card-name"><c:out value="${param.name}"/></p>
        </div>
    </div>
    <p class="user-card-bio"><c:out value="${param.bio}"/></p>
    <div class="user-card-stats">
        <div class="user-card-stat">
            <span class="user-card-stat-value">${param.reviewAmount}</span>
            <span><spring:message code="label.reviews" /></span>
        </div>
        <div class="user-card-stat">
            <span class="user-card-stat-value">${param.followersAmount}</span>
            <span><spring:message code="label.followers" /></span>
        </div>
        <div class="user-card-stat">
            <span class="user-card-stat-value">${param.followingAmount}</span>
            <span><spring:message code="label.following" /></span>
        </div>
    </div>
    <c:if test="${param.verified || param.moderator}">
        <div class="user-card-badges">
            <c:if test="${param.verified}">
                <span class="user-card-badge user-card-badge-verified"><spring:message code="label.verified" /></span>
            </c:if>
            <c:if test="${param.moderator}">
                <span class="user-card-badge user-card-badge-moderator"><spring:message code="label.moderator" /></span>
            </c:if>
        </div>
    </c:if>
</div>
</a>