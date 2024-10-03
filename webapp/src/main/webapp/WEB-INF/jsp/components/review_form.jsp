<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

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
    <label for="rating"><spring:message code="label.rating" /></label>
    <form:input path="rating" id="rating" type="text" />
    <form:errors path="rating" cssClass="error" />
  </div>
  <div class="form-group">
    <button type="submit" ><spring:message code="label.submit.review" /></button>
  </div>
</form:form>