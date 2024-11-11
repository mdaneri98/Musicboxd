<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<section class="user-profile-header">
    <div class="user-profile-main">
        <c:url var="userUrl" value="/user/${param.id}"/>
        <c:url var="userImgURL" value="/images/${param.imgId}"/>
        <a href="${userUrl}" class="user-profile-image-link">
            <img src="${userImgURL}" alt="<c:out value="${param.username}" />" class="user-profile-image">
        </a>
        <div class="user-profile-info">
            <div class="entity-type">
                <spring:message code="label.user" />
            </div>
            <div class="user-badges">
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
            <a href="${userUrl}" class="user-profile-name">
                <h1>@<c:out value="${param.username}"/></h1>
                <div class="user-name-badges">
                    <h3><c:out value="${param.name}"/></h3>
                </div>
            </a>
            <p class="user-profile-bio"><c:out value="${param.bio}"/></p>

            <div class="user-profile-stats">
                <c:url var="followersInfoUrl" value="/user/${param.id}/follow-info?page=followers"/>
                <c:url var="followingInfoUrl" value="/user/${param.id}/follow-info?page=following"/>
                <c:url var="reviewPageUrl" value="/user/${param.id}?page=reviews"/>

                <a href="${reviewPageUrl}" class="stat-link">
                    <span class="stat-value"><c:out value="${param.reviewAmount}"/></span>
                    <span class="stat-label"><spring:message code="label.reviews" /></span>
                </a>

                <a href="${followersInfoUrl}" class="stat-link">
                    <span class="stat-value"><c:out value="${param.followersAmount}"/></span>
                    <span class="stat-label"><spring:message code="label.followers" /></span>
                </a>

                <a href="${followingInfoUrl}" class="stat-link">
                    <span class="stat-value"><c:out value="${param.followingAmount}"/></span>
                    <span class="stat-label"><spring:message code="label.following" /></span>
                </a>
            </div>
        </div>
    </div>
</section>