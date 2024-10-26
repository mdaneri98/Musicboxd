<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <spring:message var="pageTitle" code="page.following.info"/>
  <jsp:include page="/WEB-INF/jsp/components/head.jsp">
    <jsp:param name="title" value="${pageTitle}"/>
  </jsp:include>

  <c:url var="review_card" value="/static/css/review_card.css" />
  <link rel="stylesheet" href="${review_card}">
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
<div>
  <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
    <jsp:param name="loggedUserImgId" value="${loggedUser.imgId}"/>
    <jsp:param name="moderator" value="${loggedUser.moderator}"/>
  </jsp:include>
</div>
<div class="container">

  <c:url value="/user/${user.id}/follow-info/${pageNum + 1}" var="nextPage" />
  <c:url value="/user/${user.id}/follow-info/${pageNum -1}" var="prevPage" />

  <header>
    <jsp:include page="/WEB-INF/jsp/components/user_info.jsp">
      <jsp:param name="imgId" value="${user.imgId}" />
      <jsp:param name="username" value="${user.username}" />
      <jsp:param name="name" value="${user.name}" />
      <jsp:param name="bio" value="${user.bio}" />
      <jsp:param name="reviewAmount" value="${user.reviewAmount}" />
      <jsp:param name="followersAmount" value="${user.followersAmount}" />
      <jsp:param name="followingAmount" value="${user.followingAmount}" />
      <jsp:param name="id" value="${user.id}" />
    </jsp:include>
  </header>

  <c:choose>
    <c:when test="${followersActive}">
      <h1><spring:message code="label.followers"/></h1>
    </c:when>
    <c:otherwise>
      <h1><spring:message code="label.following"/></h1>
    </c:otherwise>
  </c:choose>

  <div class="user-cards-grid">
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

  <c:url value="/user/${user.id}/follow-info/?pageNum=${pageNum + 1}&page=${followersActive ? 'followers' : 'following'}" var="nextPage" />
  <c:url value="/user/${user.id}/follow-info/?pageNum=${pageNum - 1}&page=${followersActive ? 'followers' : 'following'}" var="prevPage" />
    <c:if test="${showPrevious}"><a href="${prevPage}"><button><spring:message code="button.previous.page"/></button></a></c:if>
    <c:if test="${showNext}"><a href="${nextPage}"><button><spring:message code="button.next.page"/></button></a></c:if>

  <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
</div>



</body>
