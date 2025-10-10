package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;

import java.util.List;

public interface NotificationService {
    Void notifyLike(Review reviewId, User likedByUser);
    Void notifyComment(Review reviewId, User commentedByUser);
    Void notifyFollow(User followedUserId, User follower);
    Void notifyNewReview(Review reviewId, User reviewer);
    
    List<Notification> getUserNotifications(Long userId, Integer page, Integer pageSize);
    Void markAsRead(Long notificationId);
    Void markAllAsRead(Long userId);
    Integer getUnreadCount(Long userId);
} 