<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>

  <spring:message var="pageTitle" text="User Page"/>
  <jsp:include page="/WEB-INF/jsp/components/head.jsp">
    <jsp:param name="title" value="${pageTitle}"/>
  </jsp:include>

  <c:url var="css" value="/static/css/artist.css" />
  <link rel="stylesheet" href="${css}">

</head>
<body>
<div>
  <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
    <jsp:param name="loggedUserImgId" value="${loggedUser.imgId}"/>
  </jsp:include>
</div>
<div class="main-content container">
<div class="card-deck">
  <c:url var="profileUrl" value="/user/${user.id}"/>
  <a href="${profileUrl}"><h2>@${user.username} ${title}</h2></a>
  <c:forEach var="user_item" items="${userList}">
    <jsp:include page="/WEB-INF/jsp/components/user_card.jsp">
      <jsp:param name="imgId" value="${user_item.imgId}"/>
      <jsp:param name="username" value="@${user_item.username}"/>
      <jsp:param name="name" value="${user_item.name}"/>
      <jsp:param name="bio" value="${user_item.bio}"/>
      <jsp:param name="followersAmount" value="${user_item.followersAmount}"/>
      <jsp:param name="followingAmount" value="${user_item.followingAmount}"/>
      <jsp:param name="reviewAmount" value="${user_item.reviewAmount}"/>
      <jsp:param name="verified" value="${user_item.verified}"/>
      <jsp:param name="moderator" value="${user_item.moderator}"/>
      <jsp:param name="id" value="${user_item.id}"/>
    </jsp:include>
  </c:forEach>
</div>
</div>
</body>