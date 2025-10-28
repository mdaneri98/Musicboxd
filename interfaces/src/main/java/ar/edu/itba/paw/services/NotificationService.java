package ar.edu.itba.paw.services;


import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.dtos.NotificationDTO;
import java.util.List;

public interface NotificationService extends CrudService<NotificationDTO> {
    Void notifyLike(Review review, User likedByUser);
    Void notifyComment(Review review, User commentedByUser);
    Void notifyFollow(User followedUser, User follower);
    Void notifyNewReview(Review review);

    List<NotificationDTO> findBySubstring(String substring, Integer page, Integer pageSize);
    Long countAll();
    Long countByUserId(Long userId);
    

    List<NotificationDTO> getUserNotifications(Long userId, Integer page, Integer pageSize);
    Void markAsRead(Long notificationId);
    Void markAllAsRead(Long userId);
    Integer getUnreadCount(Long userId);
} 