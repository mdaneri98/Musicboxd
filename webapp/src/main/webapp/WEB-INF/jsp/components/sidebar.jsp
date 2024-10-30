<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<!-- Sidebar css -->
<c:url var="sidebarcssUrl" value="/static/css/sidebar.css"/>
<link rel="stylesheet" href="${sidebarcssUrl}">

<!-- Incluir Font Awesome para los iconos -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">

<div class="sidebar">
  <ul>
    <!-- Icons Section -->
    <li>
      <c:url var="homeUrl" value="/"/>
      <a href="${homeUrl}" class="sidebar-icon">
        <i class="fas fa-home"></i>
      </a>
    </li>
    <c:if test="${not empty param.loggedUserImgId}">
    <li>
      <c:url var="musicUrl" value="/music"/>
      <a href="${musicUrl}" class="sidebar-icon profile-icon">
        <c:url var="profileImageUrl" value="/images/${param.loggedUserImgId}"/>
        <i class="fas fa-music"></i>
      </a>
    </li>
    <li>
      <c:url var="musicSearchUrl" value="/search"/>
      <a href="${musicSearchUrl}" class="sidebar-icon">
        <i class="fas fa-search"></i>
      </a>
    </li>
    <c:if test="${param.moderator}">
      <li>
        <c:url var="moderatorUrl" value="/mod"/>
        <a href="${moderatorUrl}" class="sidebar-icon">
          <i class="fas fa-plus-square"></i>
        </a>
      </li>
    </c:if>
    <li>
      <c:url var="profileUrl" value="/user/profile"/>
      <a href="${profileUrl}" class="sidebar-icon profile-icon">
        <c:url var="profileImageUrl" value="/images/${param.loggedUserImgId}"/>
        <img src="${profileImageUrl}" alt="Profile">
      </a>
    </li>

    <!-- Language Toggle -->
    <li>
      <c:choose>
        <c:when test="${loggedUser.preferredLanguage eq 'es'}">
          <c:url var="changeLanguageUrl" value="/user/language/en" />
          <form action="${changeLanguageUrl}" method="post" style="display: inline;">
            <button type="submit" class="sidebar-icon" style="border: none; background: none; cursor: pointer;">
              <i class="fas fa-language"></i>
              <span style="font-size: 0.8em;">EN</span>
            </button>
          </form>
        </c:when>
        <c:otherwise>
          <c:url var="changeLanguageUrl" value="/user/language/es" />
          <form action="${changeLanguageUrl}" method="post" style="display: inline;">
            <button type="submit" class="sidebar-icon" style="border: none; background: none; cursor: pointer;">
              <i class="fas fa-language"></i>
              <span style="font-size: 0.8em;">ES</span>
            </button>
          </form>
        </c:otherwise>
      </c:choose>
    </li>

    <li>
      <c:url var="logoutUrl" value="/user/logout"/>
      <a href="${logoutUrl}" class="sidebar-icon">
        <i class="fas fa-sign-out-alt"></i>
      </a>
    </li>
  </ul>
  </c:if>
  <c:if test="${empty param.loggedUserImgId}">
    <li>
      <c:url var="loginUrl" value="/user/login"/>
      <a href="${loginUrl}" class="sidebar-icon">
        <i class="fa-solid fa-right-to-bracket"></i>
      </a>
    </li>
  </c:if>
</div>