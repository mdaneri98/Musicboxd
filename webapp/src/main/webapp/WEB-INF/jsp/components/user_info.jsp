<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url var="userUrl" value="/user/${param.id}"/>
<div class="info-container">
    <c:url var="userImgURL" value="/images/${param.imgId}"/>
    <a href="${userUrl}">
    <img src="${userImgURL}" alt="User Name" class="primary-image"></a>
    <div class="data-container">
        <p class="type"><spring:message code="label.user" /></p>
        <a href="${userUrl}"><h1>@<c:out value="${param.username}"/></h1></a>
        <a href="${userUrl}"><h3><c:out value="${param.name}"/></h3></a>
        <p class="artist-bio"><c:out value="${param.bio}"/></p>
        <div class="user-stats">
            <c:url var="followersInfoUrl" value="/user/${param.id}/follow-info?page=followers"/>
            <c:url var="followingInfoUrl" value="/user/${param.id}/follow-info?page=following"/>
            <span class="stat-item">
                <a href="${userUrl}"><strong><c:out value="${param.reviewAmount}"/></strong> <spring:message code="label.reviews" /></a>
            </span>
            <span class="stat-item">
                <a href="${followersInfoUrl}"><strong><c:out value="${param.followersAmount}"/></strong> <spring:message code="label.followers" /></a>
            </span>
            <span class="stat-item">
                <a href="${followingInfoUrl}"><strong><c:out value="${param.followingAmount}"/></strong> <spring:message code="label.following" /></a>
            </span>
        </div>
    </div>
</div>