<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="info-container">
  <c:url var="artistImgURL" value="/images/${param.imgId}"/>
  <img src="${artistImgURL}" alt="Artist Name" class="primary-image">
  <div class="data-container">
    <p class="type">Artist</p>
    <div>
    <h1><c:out value="${param.name}"/></h1>
    <p class="artist-bio"><c:out value="${param.bio}"/></p>
    </div>
  </div>
  <div class="data-container">
    <c:url value="/artist/${param.id}/reviews" var="new_artist_review_url" />
    <a href="${new_artist_review_url}">
      <button>Make a review</button>
    </a>
    <c:url value="/artist/${param.id}/add-favorite" var="add_favorite_url" />
    <c:url value="/artist/${param.id}/remove-favorite" var="remove_favorite_url" />
    <c:choose>
      <c:when test="${!param.isFavorite}">
        <a href="${add_favorite_url}">
          <button type="submit">Add to favorites</button>
        </a>
      </c:when>
      <c:otherwise>
        <a href="${remove_favorite_url}">
          <button type="submit">Remove from favorites</button>
        </a>
      </c:otherwise>
    </c:choose>
  </div>
</div>
