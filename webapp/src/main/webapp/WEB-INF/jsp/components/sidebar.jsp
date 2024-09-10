<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${pageTitle} - Tu Aplicación</title>
  <style>
    :root {
      --background-base: #121212;
      --background-highlight: #1a1a1a;
      --background-press: #000;
      --text-base: #fff;
      --text-subdued: #a7a7a7;
      --essential-bright-accent: #1ed760;
      --sidebar-width: 80px;
    }

    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
    }

    body {
      font-family: 'Circular Std', Arial, sans-serif;
      background-color: var(--background-base);
      color: var(--text-base);
      display: flex;
      height: 100vh;
    }

    .sidebar {
      width: var(--sidebar-width);
      height: 100%;
      background-color: var(--background-press);
      padding: 20px 0;
      display: flex;
      flex-direction: column;
      align-items: center;
      border-right: 1px solid var(--background-highlight);
    }

    .sidebar-icon {
      display: flex;
      justify-content: center;
      align-items: center;
      width: 48px;
      height: 48px;
      color: var(--text-subdued);
      text-decoration: none;
      font-size: 24px;
      margin-bottom: 20px;
      border-radius: 50%;
      transition: color 0.3s ease, background-color 0.3s ease;
    }

    .sidebar-icon:hover, .sidebar-icon.active {
      color: var(--text-base);
      background-color: var(--background-highlight);
    }

    .profile-icon {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      overflow: hidden;
      margin-top: auto;
    }

    .profile-icon img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .main-content {
      flex-grow: 1;
      padding: 24px;
      overflow-y: auto;
      background: linear-gradient(to bottom, #1e3c72, #2a5298);
    }
  </style>
  <!-- Incluir Font Awesome para los iconos -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>
<div class="sidebar">
  <a href="/home" class="sidebar-icon ${currentPage == 'home' ? 'active' : ''}">
    <i class="fas fa-home"></i>
  </a>
  <a href="/search" class="sidebar-icon ${currentPage == 'search' ? 'active' : ''}">
    <i class="fas fa-search"></i>
  </a>
  <a href="/library" class="sidebar-icon ${currentPage == 'library' ? 'active' : ''}">
    <i class="fas fa-book"></i>
  </a>
  <a href="/playlist/create" class="sidebar-icon ${currentPage == 'create-playlist' ? 'active' : ''}">
    <i class="fas fa-plus-square"></i>
  </a>
  <a href="/liked-songs" class="sidebar-icon ${currentPage == 'liked-songs' ? 'active' : ''}">
    <i class="fas fa-heart"></i>
  </a>
  <a href="/profile" class="sidebar-icon profile-icon">
    <img src="/api/user/profile-image" alt="Profile">
  </a>
</div>

<div class="main-content">
  <!-- Aquí se insertará el contenido específico de cada página -->
  <jsp:include page="${contentPage}" />
</div>
</body>
</html>