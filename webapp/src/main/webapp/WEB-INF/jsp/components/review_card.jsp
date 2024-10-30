<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="review-card">
    <c:if test="${!param.blocked}">
        <c:url var="itemUrl" value="${param.item_url}" />
        <a href="${itemUrl}" class="review-header">
            <div class="review-image">
                <c:url var="reviewImgUrl" value="/images/${param.item_img_id}" />
                <img src="${reviewImgUrl}" alt="${param.item_name}" class="img-cover">
            </div>
            <div class="review-header-info">
                <h3 class="review-title"><c:out value="${param.item_name}"/></h3>
                <p class="review-type"><c:out value="${param.item_type}"/></p>
            </div>
            <div class="rating-display">
                <div class="star-rating">
                    <c:forEach var="i" begin="1" end="5">
                        <span class="star ${i <= param.rating ? 'filled' : ''}">&#9733;</span>
                    </c:forEach>
                </div>
            </div>
        </a>

        <div class="review-content">
            <h4 class="review-content-title"><c:out value="${param.title}"/></h4>
            <p class="review-description"><c:out value="${param.review_content}"/></p>
        </div>

        <div class="review-footer">
            <div class="user-info">
                <c:url var="userUrl" value="/user/${param.user_id}" />
                <a href="${userUrl}" class="user-link">
                    <c:url var="userImgUrl" value="/images/${param.user_img_id}" />
                    <img src="${userImgUrl}" alt="${param.user_name}" class="img-avatar">
                    <div class="user-details">
                        <span class="review-timestamp"><c:out value="${param.timeAgo}"/></span>
                        <span class="user-name"><c:out value="${param.user_name}"/></span>
                        <div class="user-badges">
                            <c:if test="${param.verified}">
                                <span class="badge badge-verified">
                                    <spring:message code="label.verified" />
                                </span>
                            </c:if>
                            <c:if test="${param.userModerator}">
                                <span class="badge badge-moderator">
                                    <spring:message code="label.moderator" />
                                </span>
                            </c:if>
                        </div>
                    </div>
                </a>
            </div>

            <div class="review-actions">
                <!-- Like action -->
                <div class="action-item">
                    <c:url var="likeReviewLink" value="/review/like/${param.review_id}" />
                    <c:url var="removeLikeReviewLink" value="/review/remove-like/${param.review_id}" />
                    <span class="action-count">${param.likes}</span>
                    <c:choose>
                        <c:when test="${!param.isLiked}">
                            <a href="${likeReviewLink}" class="action-link">
                                <i class="fa-regular fa-heart"></i>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${removeLikeReviewLink}" class="action-link active">
                                <i class="fa-solid fa-heart"></i>
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Comment action -->
                <div class="action-item">
                    <c:url var="reviewUrl" value="/review/${param.review_id}" />
                    <span class="action-count">${param.commentAmount}</span>
                    <a href="${reviewUrl}" class="action-link">
                        <i class="fa-regular fa-comment"></i>
                    </a>
                </div>

                <!-- Moderator actions -->
                <c:if test="${param.moderator}">
                    <div class="action-item">
                        <c:url value="/mod/block/${param.review_id}" var="blockReviewUrl"/>
                        <a href="${blockReviewUrl}" class="action-link danger">
                            <i class="fa-solid fa-ban"></i>
                        </a>
                    </div>
                </c:if>
            </div>
        </div>
    </c:if>

    <!-- Blocked review state -->
    <c:if test="${param.blocked}">
        <div class="review-content blocked">
            <h4 class="review-content-title">
                <spring:message code="label.this.review.was.blocked.by.moderator" />
            </h4>
            <p class="review-description">
                <spring:message code="label.try.making.another.one" />
            </p>
            <c:if test="${param.moderator}">
                <c:url value="/mod/unblock/${param.review_id}" var="unblockReviewUrl"/>
                <a href="${unblockReviewUrl}" class="btn btn-secondary">
                    <spring:message code="label.unblock" />
                    <i class="fa-solid fa-ban"></i>
                </a>
            </c:if>
        </div>
    </c:if>
</div>
