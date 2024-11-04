package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;

import java.util.List;

public interface NotificationDao {
    Notification create(Notification.NotificationType type, User recipientUser,
                        User triggerUser, Review review, String message);
    
    List<Notification> getNotificationsForUser(Long userId, int page, int pageSize);
    
    void markAsRead(Long notificationId);
    
    void markAllAsRead(Long userId);
    
    int getUnreadCount(Long userId);
} 