<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<c:url var="userImgURL" value="/images/${param.imgId}"/>
<img src="${userImgURL}" alt="User Name" class="artist-image">
<div class="artist-info">
    <p class="artist-type"></p>
    <h1>@<c:out value="${param.username}"/></h1>
    <h3><c:out value="${param.name}"/></h3>
    <p class="artist-bio"><c:out value="${param.bio}"/></p>
    <div class="user-stats">
      <span class="stat-item">
        <strong><c:out value="${param.reviewAmount}"/></strong> Posts
      </span>
        <span class="stat-item">
        <strong><c:out value="${param.followersAmount}"/></strong> Followers
      </span>
        <span class="stat-item">
        <strong><c:out value="${param.followingAmount}"/></strong> Following
      </span>
    </div>
    <c:url value="/user/edit" var="edit_profile_url" />
    <a href="${edit_profile_url}">
        <button>Edit Profile</button>
    </a>
</div>