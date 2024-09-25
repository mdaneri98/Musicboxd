<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url var="userUrl" value="/user/${param.id}"/>
<div class="info-container">
    <c:url var="userImgURL" value="/images/${param.imgId}"/>
    <a href="${userUrl}">
    <img src="${userImgURL}" alt="User Name" class="primary-image"></a>
    <div class="data-container">
        <p class="type">User</p>
        <a href="${userUrl}"><h1>@<c:out value="${param.username}"/></h1></a>
        <a href="${userUrl}"><h3><c:out value="${param.name}"/></h3></a>
        <p class="artist-bio"><c:out value="${param.bio}"/></p>
        <div class="user-stats">
            <c:url var="followInfoUrl" value="/user/${param.id}/follow-info"/>
            <a href="${followInfoUrl}">
                <span class="stat-item">
                    <strong><c:out value="${param.reviewAmount}"/></strong> Reviews</span>
                <span class="stat-item">
                    <strong><c:out value="${param.followersAmount}"/></strong> Followers</span>
                <span class="stat-item">
                    <strong><c:out value="${param.followingAmount}"/></strong> Following</span>
            </a>
        </div>
    </div>
</div>