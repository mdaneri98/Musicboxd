package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;

import java.util.List;

public interface NotificationService {
    void notifyLike(Review reviewId, User likedByUser);
    void notifyComment(Review reviewId, User commentedByUser);
    void notifyFollow(Long followedUserId, User follower);
    void notifyNewReview(Review reviewId, User reviewer);
    
    List<Notification> getUserNotifications(Long userId, int page, int pageSize);
    void markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
    int getUnreadCount(Long userId);
} 