<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <spring:message var="pageTitle" code="notifications.title"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${pageTitle}"/>
    </jsp:include>
</head>
<body>
<div class="main-container">
    <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
        <jsp:param name="loggedUserImgId" value="${loggedUser.image.id}"/>
        <jsp:param name="moderator" value="${loggedUser.moderator}"/>
        <jsp:param name="unreadNotificationCount" value="${loggedUser.unreadNotificationCount}"/>
    </jsp:include>
    <div class="notifications-container">
        <div class="notifications">
            <h2><spring:message code="notifications.title"/></h2>
            <c:url value="notifications/read-all" var="readAllUrl"/>
            <c:if test="${unreadCount > 0}">
                <a href="${readAllUrl}" class="mark-read-btn"><spring:message code="notifications.mark.all.read"/></a>
            </c:if>
        </div>
        <div class="notifications-list">
            <c:forEach var="notification" items="${notifications}">
                <div class="notification-item ${notification.read ? 'read' : 'unread'}">
                    <div class="notification-content-wrapper">
                        <div class="user-info">
                            <c:url var="triggerUserUrl" value="/user/${notification.triggerUser.id}" />
                            <a href="${triggerUserUrl}" class="user-details">
                                <c:url var="userImgUrl" value="/images/${notification.triggerUser.image.id}" />
                                <img src="${userImgUrl}" alt="<c:out value="${notification.triggerUser.username}" />" class="img-avatar">
                            </a>
                            
                            <div class="notification-content">
                                <c:choose>
                                    <c:when test="${notification.type == 'LIKE' || notification.type == 'COMMENT' || notification.type == 'NEW_REVIEW'}">
                                        <c:url value="review/${notification.review.id}" var="reviewUrl"/>
                                        <a href="${reviewUrl}" class="notification-review-link">
                                            <div class="notification-text">
                                                <p><spring:message code="${notification.message}" arguments="${notification.triggerUser.name},${notification.review.itemName}"/></p>
                                                <span class="notification-time"><c:out value="${notification.timeAgo}"/></span>
                                            </div>
                                        </a>
                                    </c:when>
                                    <c:when test="${notification.type == 'FOLLOW'}">
                                        <a href="${triggerUserUrl}">
                                            <p><spring:message code="${notification.message}" arguments="${notification.triggerUser.name}"/></p>
                                            <span class="notification-time"><c:out value="${notification.timeAgo}"/></span>
                                        </a>
                                    </c:when>
                                </c:choose>
                            </div>
                        </div>

                        <c:if test="${notification.type == 'LIKE' || notification.type == 'COMMENT' || notification.type == 'NEW_REVIEW'}">
                            <a href="${reviewUrl}">
                            <c:url value="/images/${notification.review.itemImage.id}" var="reviewImgUrl"/>
                            <div class="notification-review-image">
                                <img src="${reviewImgUrl}" alt="${notification.review.itemName}"/>
                                </div>
                            </a>
                        </c:if>
                    </div>
                    <c:if test="${!notification.read}">
                        <c:url value="/notifications/${notification.id}/read" var="readUrl"/>
                        <a href="${readUrl}" class="mark-read-btn"><spring:message code="notifications.mark.as.read"/></a>
                    </c:if>
                </div>
            </c:forEach>
        </div>

        <c:if test="${notifications.size() == 0}">
            <p class="no-results">
                <spring:message code="label.no.results"/>
            </p>
        </c:if>
        
        <!-- Paginación -->
        <div class="pagination">
            <c:url value="/notifications?pageNum=${pageNum + 1}" var="nextPage" />
            <c:url value="/notifications?pageNum=${pageNum - 1}" var="prevPage" />
            <c:if test="${showPrevious}">
                <a href="${prevPage}" class="btn btn-secondary">
                    <spring:message code="button.previous.page" />
                </a>
            </c:if>
            <c:if test="${showNext}">
                <a href="${nextPage}" class="btn btn-secondary">
                    <spring:message code="button.next.page" />
                </a>
            </c:if>
        </div>
    </div>
    <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
</div>
</body>
</html> 