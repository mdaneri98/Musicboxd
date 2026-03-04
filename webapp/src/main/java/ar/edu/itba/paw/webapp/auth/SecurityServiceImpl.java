package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.domain.review.Review;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.services.CommentService;
import ar.edu.itba.paw.services.NotificationService;
import ar.edu.itba.paw.usecases.review.ReviewApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private ReviewApplicationService reviewApplicationService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public boolean isCurrentUser(Long userId, Authentication authentication) {
        if (authentication == null || userId == null) {
            return false;
        }
        try {
            Long principalId = Long.parseLong(authentication.getName());
            return principalId.equals(userId);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean isReviewOwner(Long reviewId, Authentication authentication) {
        if (authentication == null || reviewId == null) {
            return false;
        }
        try {
            Long principalId = Long.parseLong(authentication.getName());
            Review review = reviewApplicationService.getReviewById(reviewId);
            return review != null && review.getUserId().getValue().equals(principalId);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean isCommentOwner(Long commentId, Authentication authentication) {
        if (authentication == null || commentId == null) {
            return false;
        }
        try {
            Long principalId = Long.parseLong(authentication.getName());
            Comment comment = commentService.findById(commentId);
            return comment != null && comment.getUser().getId().equals(principalId);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean isNotificationOwner(Long notificationId, Authentication authentication) {
        if (authentication == null || notificationId == null) {
            return false;
        }
        try {
            Long principalId = Long.parseLong(authentication.getName());
            ar.edu.itba.paw.models.Notification notification = notificationService.findById(notificationId);
            return notification != null && notification.getRecipientUser().getId().equals(principalId);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
