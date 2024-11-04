<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <spring:message var="pageTitle" code="webpage.name"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${pageTitle}"/>
    </jsp:include>
</head>
<body>
    <c:url var="loggedUserImgUrl" value="/images/${loggedUser.image.id}"/>

    <div class="main-container">
        <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
            <jsp:param name="loggedUserImgId" value="${loggedUser.image.id}"/>
            <jsp:param name="moderator" value="${loggedUser.moderator}"/>
            <jsp:param name="unreadNotificationCount" value="${loggedUser.unreadNotificationCount}"/>
        </jsp:include>
        <div class="settings-container">
            <div class="settings-header">
                <h1 class="settings-title"><spring:message code="settings.title"/></h1>
                <p class="settings-description"><spring:message code="settings.description"/></p>
            </div>

            <div class="settings-content">
                <!-- Appearance Section -->
                <section class="settings-section">
                    <h2 class="section-title"><spring:message code="settings.appearance.title"/></h2>
                    <div class="settings-card">
                        <div class="settings-option">
                            <div class="option-info">
                                <h3><spring:message code="settings.theme.title"/></h3>
                                <p><spring:message code="settings.theme.description"/></p>
                            </div>
                            <div class="theme-selector">
                                <button class="theme-btn active" data-theme="dark">
                                    <i class="fas fa-moon"></i>
                                    <spring:message code="settings.theme.dark"/>
                                </button>
                            </div>
                        </div>
                    </div>
                </section>

                <!-- Language Section -->
                <section class="settings-section">
                    <h2 class="section-title"><spring:message code="settings.language.title"/></h2>
                    <div class="settings-card">
                        <div class="settings-option">
                            <div class="option-info">
                                <h3><spring:message code="settings.language.select"/></h3>
                                <p><spring:message code="settings.language.description"/></p>
                            </div>
                            <c:url var="changeLangEs" value="/user/language/es"/>
                            <c:url var="changeLangEn" value="/user/language/en"/>

                            <div class="language-selector">
                                <form action="${changeLangEn}" method="post" style="display: inline;">
                                    <button type="submit" class="language-btn ${loggedUser.preferredLanguage == "en" ? "active":""}">
                                        English
                                    </button>
                                </form>
                                <form action="${changeLangEs}" method="post" style="display: inline;">
                                    <button type="submit" class="language-btn ${loggedUser.preferredLanguage == "es" ? "active":""}">
                                        Castellano
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </section>
            </div>
        </div>
    </div>
</body>
</html>