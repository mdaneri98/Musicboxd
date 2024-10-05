<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<style>
  .star-rating-container {
    margin-top: 5px;
  }

  .star-rating {
    display: inline-flex;
    flex-direction: row-reverse;
    justify-content: flex-end;
  }

  .star-rating input[type="radio"] {
    display: none;
  }

  .star-rating label {
    color: #ccc;
    cursor: pointer;
    font-size: 24px;
    padding: 0 2px;
    transition: color 0.2s ease-in-out;
  }

  .star-rating label:hover,
  .star-rating label:hover ~ label,
  .star-rating input[type="radio"]:checked ~ label {
    color: #ffd700;
  }
</style>

<form:form modelAttribute="reviewForm" action="${param.posturl}" method="POST" class="review-form">
  <div class="form-group">
    <label for="title"><spring:message code="label.title" /></label>
    <form:input path="title" id="title" type="text" />
    <form:errors path="title" cssClass="error" />
  </div>
  <div class="form-group">
    <label for="description"><spring:message code="label.desc" /></label>
    <form:textarea path="description" id="description" rows="4" />
    <form:errors path="description" cssClass="error" />
  </div>
  <div class="form-group">
    <label><spring:message code="label.rating" /></label>
    <div class="star-rating-container">
      <div class="star-rating">
        <c:forEach var="i" begin="1" end="5">
          <input type="radio" id="star${i}" name="rating" value="${6 - i}" />
          <label for="star${i}">&#9733;</label>
        </c:forEach>
      </div>
    </div>
    <form:errors path="rating" cssClass="error" />
  </div>
  <div class="form-group">
    <button type="submit"><spring:message code="label.submit.review" /></button>
  </div>
</form:form>

<script>
  document.querySelectorAll('.star-rating input[type="radio"]').forEach(function(radio) {
    radio.addEventListener('change', function() {
      document.getElementById('rating').value = this.value;
    });
  });
</script>