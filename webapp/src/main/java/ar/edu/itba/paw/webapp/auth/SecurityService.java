package ar.edu.itba.paw.webapp.auth;

import org.springframework.security.core.Authentication;

public interface SecurityService {

    boolean isCurrentUser(Long userId, Authentication authentication);

    boolean isReviewOwner(Long reviewId, Authentication authentication);

    boolean isCommentOwner(Long commentId, Authentication authentication);

    boolean isNotificationOwner(Long notificationId, Authentication authentication);
}
