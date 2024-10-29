<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <spring:message var="pageTitle" code="page.following.info"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${pageTitle}"/>
    </jsp:include>
</head>
<body>
    <div class="main-container">
        <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
            <jsp:param name="loggedUserImgId" value="${loggedUser.image.id}"/>
            <jsp:param name="moderator" value="${loggedUser.moderator}"/>
        </jsp:include>

        <main class="content-wrapper">
            <!-- User Info Header -->
            <header>
                <jsp:include page="/WEB-INF/jsp/components/user_info.jsp">
                    <jsp:param name="imgId" value="${user.image.id}" />
                    <jsp:param name="username" value="${user.username}" />
                    <jsp:param name="name" value="${user.name}" />
                    <jsp:param name="bio" value="${user.bio}" />
                    <jsp:param name="reviewAmount" value="${user.reviewAmount}" />
                    <jsp:param name="followersAmount" value="${user.followersAmount}" />
                    <jsp:param name="followingAmount" value="${user.followingAmount}" />
                    <jsp:param name="id" value="${user.id}" />
                </jsp:include>
            </header>

            <!-- Section Title -->
            <h1 class="page-title">
                <c:choose>
                    <c:when test="${followersActive}">
                        <spring:message code="label.followers"/>
                    </c:when>
                    <c:otherwise>
                        <spring:message code="label.following"/>
                    </c:otherwise>
                </c:choose>
            </h1>

            <!-- Users Grid -->
            <div class="users-grid">
                <c:forEach var="user_item" items="${userList}">
                    <jsp:include page="/WEB-INF/jsp/components/user_card.jsp">
                        <jsp:param name="imgId" value="${user_item.image.id}"/>
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

            <!-- Pagination -->
            <div class="pagination">
                <c:url value="/user/${user.id}/follow-info/?pageNum=${pageNum + 1}&page=${followersActive ? 'followers' : 'following'}" var="nextPage" />
                <c:url value="/user/${user.id}/follow-info/?pageNum=${pageNum - 1}&page=${followersActive ? 'followers' : 'following'}" var="prevPage" />
                <c:if test="${showPrevious}">
                    <a href="${prevPage}" class="btn btn-secondary">
                        <spring:message code="button.previous.page"/>
                    </a>
                </c:if>
                <c:if test="${showNext}">
                    <a href="${nextPage}" class="btn btn-secondary">
                        <spring:message code="button.next.page"/>
                    </a>
                </c:if>
            </div>

            <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
        </main>
    </div>
</body>
</html>
