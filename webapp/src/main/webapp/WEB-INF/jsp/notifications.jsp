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
        <h2><spring:message code="notifications.title"/></h2>
        <div class="notifications-list">
            <c:forEach var="notification" items="${notifications}">
                <div class="notification-item ${notification.read ? 'read' : 'unread'}">
                    <div class="notification-content">
                        <p><c:out value="${notification.message}"/></p>
                        <span class="notification-time"><c:out value="${notification.timeAgo}"/></span>
                    </div>
                    <c:if test="${!notification.read}">
                        <c:url value="/notifications/${notification.id}/read" var="readUrl"/>
                        <a class="mark-read-btn" href="${readUrl}"><spring:message code="notifications.mark.as.read"/></a>
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
            <!-- Implementar paginación -->
        </div>
    </div>
    <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
</div>
</body>
</html> 