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
                            <c:url var="updateThemeUrl" value="/user/settings/theme"/>
                            <form action="${updateThemeUrl}" method="post" class="theme-form">
                                <select name="theme" class="theme-select" onchange="this.form.submit()">
                                    <option value="dark" ${loggedUser.theme == 'dark' ? 'selected' : ''}>
                                        <spring:message code="settings.theme.dark"/>
                                    </option>
                                    <option value="kawaii" ${loggedUser.theme == 'kawaii' ? 'selected' : ''}>
                                        Kawaii
                                    </option>
                                    <option value="sepia" ${loggedUser.theme == 'sepia' ? 'selected' : ''}>
                                        <spring:message code="settings.theme.sepia"/>
                                    </option>
                                    <option value="ocean" ${loggedUser.theme == 'ocean' ? 'selected' : ''}>
                                        <spring:message code="settings.theme.ocean"/>
                                    </option>
                                    <option value="forest" ${loggedUser.theme == 'forest' ? 'selected' : ''}>
                                        <spring:message code="settings.theme.forest"/>
                                    </option>
                                </select>
                            </form>
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
                            <c:url var="updateLanguageUrl" value="/user/language"/>
                            <form action="${updateLanguageUrl}" method="post" class="theme-form">
                                <select name="language" class="theme-select" onchange="this.form.submit()">
                                    <option value="en" ${loggedUser.preferredLanguage == 'en' ? 'selected' : ''}><spring:message code="settings.language.english"/></option>
                                    <option value="es" ${loggedUser.preferredLanguage == 'es' ? 'selected' : ''}><spring:message code="settings.language.spanish"/></option>
                                    <option value="fr" ${loggedUser.preferredLanguage == 'fr' ? 'selected' : ''}><spring:message code="settings.language.french"/></option>
                                    <option value="de" ${loggedUser.preferredLanguage == 'de' ? 'selected' : ''}><spring:message code="settings.language.german"/></option>
                                    <option value="it" ${loggedUser.preferredLanguage == 'it' ? 'selected' : ''}><spring:message code="settings.language.italian"/></option>
                                    <option value="pt" ${loggedUser.preferredLanguage == 'pt' ? 'selected' : ''}><spring:message code="settings.language.portuguese"/></option>
                                    <option value="ja" ${loggedUser.preferredLanguage == 'ja' ? 'selected' : ''}><spring:message code="settings.language.japanese"/></option>
                                </select>
                            </form>
                        </div>
                    </div>
                </section>

                <!-- Notifications Section -->
                <section class="settings-section">
                    <h2 class="section-title"><spring:message code="settings.notifications.title"/></h2>
                    <div class="settings-card">
                        <div class="settings-option">
                            <div class="option-info">
                                <h3><spring:message code="settings.notifications.follows"/></h3>
                                <p><spring:message code="settings.notifications.follows.description"/></p>
                            </div>
                            <c:url var="toggleFollowNotif" value="/user/settings/notifications/follow">
                                <c:param name="enabled" value="${!loggedUser.followNotificationsEnabled}"/>
                            </c:url>
                            <form action="${toggleFollowNotif}" method="post">
                                <div class="toggle-switch">
                                    <input type="checkbox" id="followNotif" class="toggle-input" 
                                           ${loggedUser.followNotificationsEnabled ? "checked" : ""} 
                                           onChange="this.form.submit()">
                                    <label for="followNotif" class="toggle-label"></label>
                                </div>
                            </form>
                        </div>
                        
                        <div class="settings-option">
                            <div class="option-info">
                                <h3><spring:message code="settings.notifications.likes"/></h3>
                                <p><spring:message code="settings.notifications.likes.description"/></p>
                            </div>
                            <c:url var="toggleLikeNotif" value="/user/settings/notifications/like">
                                <c:param name="enabled" value="${!loggedUser.likeNotificationsEnabled}"/>
                            </c:url>
                            <form action="${toggleLikeNotif}" method="post">
                                <div class="toggle-switch">
                                    <input type="checkbox" id="likeNotif" class="toggle-input" 
                                           ${loggedUser.likeNotificationsEnabled ? "checked" : ""} 
                                           onChange="this.form.submit()">
                                    <label for="likeNotif" class="toggle-label"></label>
                                </div>
                            </form>
                        </div>
                        
                        <div class="settings-option">
                            <div class="option-info">
                                <h3><spring:message code="settings.notifications.comments"/></h3>
                                <p><spring:message code="settings.notifications.comments.description"/></p>
                            </div>
                            <c:url var="toggleCommentNotif" value="/user/settings/notifications/comment">
                                <c:param name="enabled" value="${!loggedUser.commentNotificationsEnabled}"/>
                            </c:url>
                            <form action="${toggleCommentNotif}" method="post">
                                <div class="toggle-switch">
                                    <input type="checkbox" id="commentNotif" class="toggle-input" 
                                           ${loggedUser.commentNotificationsEnabled ? "checked" : ""} 
                                           onChange="this.form.submit()">
                                    <label for="commentNotif" class="toggle-label"></label>
                                </div>
                            </form>
                        </div>
                        
                        <div class="settings-option">
                            <div class="option-info">
                                <h3><spring:message code="settings.notifications.reviews"/></h3>
                                <p><spring:message code="settings.notifications.reviews.description"/></p>
                            </div>
                            <c:url var="toggleReviewNotif" value="/user/settings/notifications/review">
                                <c:param name="enabled" value="${!loggedUser.reviewNotificationsEnabled}"/>
                            </c:url>
                            <form action="${toggleReviewNotif}" method="post">
                                <div class="toggle-switch">
                                    <input type="checkbox" id="reviewNotif" class="toggle-input" 
                                           ${loggedUser.reviewNotificationsEnabled ? "checked" : ""} 
                                           onChange="this.form.submit()">
                                    <label for="reviewNotif" class="toggle-label"></label>
                                </div>
                            </form>
                        </div>
                    </div>
                </section>
            </div>
        </div>         
        <div class="footer-placeholder">
            <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
        </div>
    </div>

    <!-- Formulario oculto para enviar el tema -->
    <form id="themeForm" action="${pageContext.request.contextPath}/user/settings/theme" method="post" style="display: none;">
        <input type="hidden" name="theme" id="themeInput">
    </form>

    <script>
        // Agregar listener para los botones de tema
        document.addEventListener('DOMContentLoaded', () => {
            document.querySelector('.theme-selector').addEventListener('click', (e) => {
                const themeBtn = e.target.closest('.theme-btn');
                if (themeBtn) {
                    const theme = themeBtn.getAttribute('data-theme');
                    document.getElementById('themeInput').value = theme;
                    document.getElementById('themeForm').submit();
                }
            });
        });
    </script>
</body>
</html>