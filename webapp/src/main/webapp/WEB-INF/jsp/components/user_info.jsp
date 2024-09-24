<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="info-container">
    <c:url var="userImgURL" value="/images/${param.imgId}"/>
    <img src="${userImgURL}" alt="User Name" class="primary-image">
    <div class="data-container">
        <p class="artist-type">User</p>
        <h1>@<c:out value="${param.username}"/></h1>
        <h3><c:out value="${param.name}"/></h3>
        <p class="artist-bio"><c:out value="${param.bio}"/></p>
        <div class="user-stats">
          <span class="stat-item">
            <strong><c:out value="${param.reviewAmount}"/></strong> Reviews
          </span>
            <c:url var="followingUrl" value="/user/${param.id}/following"/>
            <c:url var="followersUrl" value="/user/${param.id}/followers"/>
            <span class="stat-item">
            <a href="${followersUrl}"><strong><c:out value="${param.followersAmount}"/></strong> Followers</a>
          </span>
            <span class="stat-item">
            <a href="${followingUrl}"><strong><c:out value="${param.followingAmount}"/></strong> Following</a>
          </span>
        </div>
    </div>
    <div class="data-container">
        <c:url value="/user/${param.id}/follow" var="follow_user_url" />
        <c:url value="/user/${param.id}/unfollow" var="unfollow_user_url" />
        <c:choose>
        <c:when test="${!isFollowing}">
        <form action="${follow_user_url}" method="post">
            <button type="submit">Follow</button>
        </form>
        </c:when>
        <c:otherwise>
        <form action="${unfollow_user_url}" method="post">
            <button type="submit">Unfollow</button>
        </form>
        </c:otherwise>
        </c:choose>
    </div>
</div>