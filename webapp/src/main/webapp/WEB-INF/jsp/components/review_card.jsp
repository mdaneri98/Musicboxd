<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<div class="review-container">
    <c:if test="${!param.blocked}">
        <c:url var="itemUrl" value="${param.item_url}" />
        <a href="${itemUrl}" class="review-header">
            <div>
                <c:url var="reviewImgUrl" value="/images/${param.item_img_id}" />
                <img src="${reviewImgUrl}" alt="${param.item_name} Cover" class="item-cover">
            </div>
            <div class="review-header-info">
                <h2><c:out value="${param.item_name}"/></h2>
                <p><c:out value="${param.item_type}"/></p>
            </div>
        </a>
        <div class="star-rating-container review-header">
            <div class="star-rating">
                <c:forEach var="i" begin="1" end="5">
                    <c:choose>
                        <c:when test="${i <= param.rating}">&#9733;</c:when>
                        <c:otherwise>&#9734;</c:otherwise>
                    </c:choose>
                </c:forEach>
            </div>
        </div>
        <div class="separator"></div>
        <c:url var="reviewUrl" value="/review/${param.review_id}" />
        <a href="${reviewUrl}">
            <div class="review-content">
                <div class="review-title"><c:out value="${param.title}"/></div>
                <p class="review-content"><c:out value="${param.review_content}"/></p>
            </div>
        </a>
        <div class="separator"></div>
        <div class="review-footer">
            <c:url var="userUrl" value="/user/${param.user_id}" />
            <a href="${userUrl}" class="user-info">
                <c:url var="userImgUrl" value="/images/${param.user_img_id}" />
                <img src="${userImgUrl}" alt="${param.user_name} Avatar" class="user-avatar">
                <div class="user-data">
                    <div class="name-and-date">
                        <div class="user-name"><c:out value="${param.user_name}"/></div>
                        <span class="review-timestamp"><c:out value="${param.timeAgo}"/></span>
                    </div>
                    <div class="user-card-badges">
                        <c:if test="${param.verified}">
                            <span class="user-card-badge user-card-badge-verified"><spring:message code="label.verified" /></span>
                        </c:if>
                        <c:if test="${!param.verified}">
                            <span class="user-card-badge user-card-badge-unverified"><spring:message code="label.unverified" /></span>
                        </c:if>
                        <c:if test="${param.userModerator}">
                            <span class="user-card-badge user-card-badge-moderator"><spring:message code="label.moderator" /></span>
                        </c:if>
                    </div>
                </div>
            </a>
            <div class="review-actions">
                <div class="review-icon">
                    <c:url var="likeReviewLink" value="/review/like/${param.review_id}" />
                    <c:url var="removeLikeReviewLink" value="/review/remove-like/${param.review_id}" />
                    <span> <c:out value="${param.likes}"/></span>
                    <c:choose>
                        <c:when test="${!param.isLiked}">
                            <a href="${likeReviewLink}">
                                <i class="fa-regular fa-heart"></i>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${removeLikeReviewLink}" style="color: red;">
                                <i class="fa-solid fa-heart"></i>
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="review-icon">
                    <span><c:out value="${param.commentAmount}"/> </span>
                    <a href="${reviewUrl}" class="share-button">
                        <i class="fa-regular fa-comment"></i>
                    </a>
                </div>
                <c:if test="${param.moderator}">
                    <c:url value="/mod/block/${param.review_id}" var="blockReviewUrl"/>
                    <div class="review-icon">
                        <div class="review-block-btn">
                            <a href="${blockReviewUrl}" class="btn-icon">
                                <i class="fa-solid fa-ban"></i>
                            </a>
                        </div>
                    </div>
                </c:if>
            </div>
    </c:if>
            <c:if test="${param.blocked}">
                <div class="review-content">
                    <div class="review-title"><spring:message code="label.this.review.was.blocked.by.moderator" /></div>
                    <p class="review-content"><spring:message code="label.try.making.another.one" /></p>
                </div>
                <c:if test="${param.moderator}">
                    <div class="review-block-btn">
                        <c:url value="/mod/unblock/${param.review_id}" var="unblockReviewUrl"/>
                        <a href="${unblockReviewUrl}" class="btn-icon">
                            <span><spring:message code="label.unblock" />  <i class="fa-solid fa-ban"></i></span>
                        </a>
                    </div>
                </c:if>
            </c:if>
        </div>
</div>
