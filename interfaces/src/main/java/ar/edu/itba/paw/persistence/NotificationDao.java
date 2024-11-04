package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Notification;
import java.util.List;

public interface NotificationDao {
    Notification create(Notification.NotificationType type, Long recipientUserId, 
                       Long triggerUserId, Long resourceId, String message);
    
    List<Notification> getNotificationsForUser(Long userId, int page, int pageSize);
    
    void markAsRead(Long notificationId);
    
    void markAllAsRead(Long userId);
    
    int getUnreadCount(Long userId);
} 