<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<div class="info-container">
  <c:url var="albumImgUrl" value="/images/${param['album.imgId']}"/>
  <img src="${albumImgUrl}" alt="${param['album.title']}" class="primary-image">
  <div class="data-container">
    <p class="album-type">Album</p>
    <h1><c:out value="${param['album.title']}"/></h1>
    <div class="button-group">
      <c:url var="artistUrl" value="/artist/${param['artist.id']}" />
      <a href="${artistUrl}" class="button artist-button">
        <c:url var="artistImgUrl" value="/images/${param['artist.imgId']}"/>
        <img src="${artistImgUrl}" alt="${param['artist.name']}" class="secondary-image">
        <span><c:out value="${param['artist.name']}"/></span>
      </a>
    </div>
  </div>
  <div class="data-container">
    <c:url var="albumReviewUrl" value="/album/${param['album.id']}/reviews" />
    <a href="${albumReviewUrl}" class="button primary-button">Make a review</a>
    <c:url value="/album/${param['album.id']}/add-favorite" var="add_favorite_url" />
    <c:url value="/album/${param['album.id']}/remove-favorite" var="remove_favorite_url" />
    <c:choose>
      <c:when test="${!param.isFavorite}">
        <a href="${add_favorite_url}"><button>Add to favorites</button></a>
      </c:when>
      <c:otherwise>
        <a href="${remove_favorite_url}">
          <button type="submit" class="primary-button">Remove from favorites</button>
        </a>
      </c:otherwise>
    </c:choose>
  </div>
</div>