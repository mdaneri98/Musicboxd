<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<aside class="sidebar">
    <nav class="sidebar-nav">
        <!-- Home -->
        <c:url var="homeUrl" value="/"/>
        <a href="${homeUrl}" class="sidebar-icon" title="Home">
            <i class="fas fa-home"></i>
        </a>

        <!-- Music Discovery -->
        <c:url var="musicUrl" value="/music"/>
        <a href="${musicUrl}" class="sidebar-icon" title="Music">
            <i class="fas fa-music"></i>
        </a>

        <!-- Search -->
        <c:url var="musicSearchUrl" value="/search"/>
        <a href="${musicSearchUrl}" class="sidebar-icon" title="Search">
            <i class="fas fa-search"></i>
        </a>


        <!-- Logged User Section -->
        <c:if test="${not empty param.loggedUserImgId}">

            <%--  Notifications  --%>
            <c:url var="notificationsUrl" value="/notifications"/>
            <a href="${notificationsUrl}" class="sidebar-icon" title="Notifications">
                <i class="fas fa-bell"></i>
                <c:if test="${param.unreadNotificationCount > 0}">
                    <span class="notification-badge">${param.unreadNotificationCount}</span>
                </c:if>
            </a>

            <!-- Moderator Section -->
            <c:if test="${param.moderator}">
                <c:url var="moderatorUrl" value="/mod"/>
                <a href="${moderatorUrl}" class="sidebar-icon" title="Moderator">
                    <i class="fas fa-plus-square"></i>
                </a>
            </c:if>

            <!-- Profile -->
            <c:url var="profileUrl" value="/user/profile"/>
            <a href="${profileUrl}" class="sidebar-icon profile-icon" title="Profile">
                <c:url var="profileImageUrl" value="/images/${param.loggedUserImgId}"/>
                <img src="${profileImageUrl}" alt="Profile" class="profile-image">
            </a>

            <!-- Settings -->
            <c:url var="settingsUrl" value="/user/profile/settings"/>
            <a href="${settingsUrl}" class="sidebar-icon" title="Settings">
                <i class="fas fa-cog"></i>
            </a>

            <!-- Logout -->
            <c:url var="logoutUrl" value="/user/logout"/>
            <a href="${logoutUrl}" class="sidebar-icon" title="Logout">
                <i class="fas fa-sign-out-alt"></i>
            </a>
        </c:if>

        <!-- Anonymous User Section -->
        <c:if test="${empty param.loggedUserImgId}">
            <c:url var="loginUrl" value="/user/login"/>
            <a href="${loginUrl}" class="sidebar-icon profile-icon" title="Login">
                <i class="fa-solid fa-right-to-bracket"></i>
            </a>
        </c:if>
    </nav>
</aside>
