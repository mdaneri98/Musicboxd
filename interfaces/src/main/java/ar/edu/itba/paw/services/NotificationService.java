package ar.edu.itba.paw.services;


import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.dtos.NotificationDTO;
import java.util.List;

public interface NotificationService extends CrudService<NotificationDTO> {
    // Métodos para crear notificaciones
    Void notifyLike(Review reviewId, User likedByUser);
    Void notifyComment(Review reviewId, User commentedByUser);
    Void notifyFollow(User followedUserId, User follower);
    Void notifyNewReview(Review reviewId, User reviewer);

    // Métodos para la API
    List<NotificationDTO> findBySubstring(String substring, Integer page, Integer pageSize);
    Long countAll();
    Long countByUserId(Long userId);
    
    // Métodos específicos de notificaciones
    List<NotificationDTO> getUserNotifications(Long userId, Integer page, Integer pageSize);
    Void markAsRead(Long notificationId);
    Void markAllAsRead(Long userId);
    Integer getUnreadCount(Long userId);
} 