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
      <c:url var="homeUrl" value="/home"/>
      <a href="${homeUrl}" class="sidebar-icon">
        <i class="fas fa-home"></i>
      </a>
    </li>
    <li>
      <c:url var="musicSearchUrl" value="/search"/>
      <a href="${musicSearchUrl}" class="sidebar-icon">
        <i class="fas fa-search"></i>
      </a>
    </li>
    <li>
      <c:url var="profileUrl" value="/user/profile"/>
      <a href="${profileUrl}" class="sidebar-icon profile-icon">
        <c:url var="profileImageUrl" value="/images/${param.loggedUserImgId}"/>
        <img src="${profileImageUrl}" alt="Profile">
      </a>
    </li>
    <!--
    <li>
      <a class="sidebar-icon">
        <i class="fas fa-book"></i>
      </a>
    </li>
    <li>
      <a class="sidebar-icon">
        <i class="fas fa-plus-square"></i>
      </a>
    </li>
    <li>
      <a class="sidebar-icon">
        <i class="fas fa-heart"></i>
      </a>
    </li>
    -->
    <c:if test="${param.moderator}">
      <li>
        <c:url var="moderatorUrl" value="/mod"/>
        <a href="${moderatorUrl}" class="sidebar-icon">
          <i class="fa-solid fa-bolt"></i>
        </a>
      </li>
    </c:if>
    <li>
      <c:url var="logoutUrl" value="/user/logout"/>
      <a href="${logoutUrl}" class="sidebar-icon">
        <i class="fas fa-sign-out-alt"></i>
      </a>
    </li>
  </ul>
</div>
