package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;
import java.util.List;

public interface NotificationDao extends CrudDao<Notification> {
    // Método original para crear notificaciones desde eventos
    Notification create(Notification.NotificationType type, User recipientUser,
                        User triggerUser, Review review, String message);
    
    // Métodos CRUD básicos
    List<Notification> findBySubstring(String substring, Integer page, Integer pageSize);
    Long countAll();
    Long countByUserId(Long userId, Boolean read);
    Long countByUserId(Long userId);
    
    // Métodos específicos de notificaciones
    List<Notification> getNotificationsForUser(Long userId, Integer page, Integer pageSize);
    Void markAsRead(Long notificationId);
    Void markAllAsRead(Long userId);
    Integer getUnreadCount(Long userId);
} 