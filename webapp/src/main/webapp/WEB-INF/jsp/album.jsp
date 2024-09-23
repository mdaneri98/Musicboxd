<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <spring:message var="pageTitle" text="Album Page"/>
  <jsp:include page="/WEB-INF/jsp/components/head.jsp">
    <jsp:param name="title" value="${pageTitle}"/>
  </jsp:include>

  <c:url var="css" value="/static/css/album.css" />
  <link rel="stylesheet" href="${css}">

  <c:url var="review_card" value="/static/css/review_card.css" />
  <link rel="stylesheet" href="${review_card}">
</head>
  <body>
    <div>
      <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
        <jsp:param name="loggedUserImgId" value="${loggedUser.imgId}"/>
      </jsp:include>
    </div>
    <div class="container">
      <section>
          <header class="header-content">
            <jsp:include page="/WEB-INF/jsp/components/album_info.jsp">
              <jsp:param name="album.imgId" value="${album.imgId}" />
              <jsp:param name="album.id" value="${album.id}" />
              <jsp:param name="album.title" value="${album.title}" />
              <jsp:param name="artist.id" value="${artist.id}" />
              <jsp:param name="artist.imgId" value="${artist.imgId}" />
              <jsp:param name="artist.name" value="${artist.name}" />
              <jsp:param name="isFavorite" value="${isFavorite}" />
            </jsp:include>
          </header>
      </section>
      <section id="main-section">
        <h2>Songs</h2>
        <div>
          <ul class="song-list">
            <c:forEach var="song" items="${songs}" varStatus="status">
              <li>
                <span class="song-number">${status.index + 1}</span>
                <c:url var="songUrl" value="/song/${song.id}" />
                <a href="${songUrl}" class="song-title"><c:out value="${song.title}"/></a>
              </li>
            </c:forEach>
          </ul>
        </div>

      <h2>Reviews</h2>
      <div class="cards-container">
        <c:forEach var="review" items="${reviews}">
          <jsp:include page="/WEB-INF/jsp/components/review_card.jsp">
            <jsp:param name="item_img_id" value="${review.album.imgId}"/>
            <jsp:param name="item_name" value="${review.album.title}"/>
            <jsp:param name="item_url" value="/album/${review.album.id}"/>
            <jsp:param name="item_type" value="Album"/>
            <jsp:param name="title" value="${review.title}"/>
            <jsp:param name="rating" value="${review.rating}"/>
            <jsp:param name="review_content" value="${review.description}"/>
            <jsp:param name="user_name" value="@${review.user.username}"/>
            <jsp:param name="user_img_id" value="${review.user.imgId}"/>
            <jsp:param name="likes" value="${review.likes}"/>
            <jsp:param name="user_id" value="${review.user.id}"/>
            <jsp:param name="review_id" value="${review.id}"/>
            <jsp:param name="isLiked" value="${review.liked}"/>
          </jsp:include>
        </c:forEach>
      </div>
      </section>
        <section>
            <footer>
                <div>
                  <c:url value="/album/${album.id}/${pageNum + 1}" var="nextPage" />
                  <c:url value="/album/${album.id}/${pageNum -1}" var="prevPage" />
                  <a href="${prevPage}" class="button review-button">Previous page</a>
                  <a href="${nextPage}" class="button review-button">Next page</a>
                </div>
            </footer>
        </section>
    </div>
  </body>
</html>

