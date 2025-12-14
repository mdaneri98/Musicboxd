package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.StatusType;

import java.util.List;

public interface NotificationService extends CrudService<Notification> {
    Void notifyLike(Review review, User likedByUser);
    Void notifyComment(Review review, User commentedByUser);
    Void notifyFollow(User followedUser, User follower);
    Void notifyNewReview(Review review);
    Void notifyReviewBlockStatusChange(Review review, Boolean wasBlocked, Boolean isBlocked);

    List<Notification> findBySubstring(String substring, Integer page, Integer pageSize);
    Long countAll();
    Long countByUserId(Long userId, StatusType statusType);

    List<Notification> getUserNotifications(Long userId, Integer page, Integer pageSize, StatusType status);
    Void markAsRead(Long notificationId);
    Void markAllAsRead(Long userId);
    Integer getUnreadCount(Long userId);
}
