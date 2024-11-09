<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url var="profileUrl" value="/user/${param.id}"/>
<a href="${profileUrl}" class="user-card">
    <div class="user-card-header">
        <c:url var="userImgUrl" value="/images/${param.imgId}"/>
        <img src="${userImgUrl}" alt="${param.username}" class="user-card-image">
        <div class="user-card-info">
            <h3 class="user-card-username"><c:out value="${param.username}"/></h3>
            <p class="user-card-name"><c:out value="${param.name}"/></p>
        </div>
        <c:if test="${param.verified || param.moderator}">
            <div class="user-card-badges">
                <c:if test="${param.verified}">
                    <span class="badge badge-verified">
                        <spring:message code="label.verified" />
                    </span>
                </c:if>
                <c:if test="${param.moderator}">
                    <span class="badge badge-moderator">
                        <spring:message code="label.moderator" />
                    </span>
                </c:if>
            </div>
        </c:if>
    </div>

    <p class="user-card-bio"><c:out value="${param.bio}"/></p>

    <div class="user-card-stats">
        <div class="user-card-stat">
            <span class="user-card-stat-value"><c:out value="${param.reviewAmount}"/></span>
            <span class="user-card-stat-label">
                <spring:message code="label.reviews" />
            </span>
        </div>
        <div class="user-card-stat">
            <span class="user-card-stat-value"><c:out value="${param.followersAmount}"/></span>
            <span class="user-card-stat-label">
                <spring:message code="label.followers" />
            </span>
        </div>
        <div class="user-card-stat">
            <span class="user-card-stat-value"><c:out value="${param.followingAmount}"/></span>
            <span class="user-card-stat-label">
                <spring:message code="label.following" />
            </span>
        </div>
    </div>
</a>