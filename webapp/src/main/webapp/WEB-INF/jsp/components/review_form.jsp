<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="review-form-container">
    <form:form modelAttribute="reviewForm" action="${param.posturl}" method="POST" class="review-form">
        <div class="form-group">
            <label for="title" class="form-label">
                <spring:message code="label.title" />
            </label>
            <form:input path="title" id="title" type="text" class="form-control"/>
            <form:errors path="title" cssClass="form-error" />
        </div>

        <div class="form-group">
            <label for="description" class="form-label">
                <spring:message code="label.desc" />
            </label>
            <form:textarea path="description" id="description" rows="4" class="form-control"/>
            <form:errors path="description" cssClass="form-error" />
        </div>

        <div class="form-group">
            <label class="form-label">
                <spring:message code="label.rating" />
            </label>
            <div class="rating-input">
                <div class="star-rating-input">
                    <c:forEach var="i" begin="1" end="5">
                        <input type="radio" id="star${i}" name="rating" value="${6 - i}" class="star-radio"/>
                        <label for="star${i}" class="star-label">&#9733;</label>
                    </c:forEach>
                </div>
                <form:errors path="rating" cssClass="form-error" />
            </div>
        </div>

        <div class="form-actions">
            <a href="${param.cancelUrl}" class="btn btn-secondary">
                <spring:message code="button.cancel"/>
            </a>
            <button type="submit" class="btn btn-primary">
                <spring:message code="label.submit.review" />
            </button>
        </div>
    </form:form>
</div>