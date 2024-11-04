package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.NotificationDao;
import ar.edu.itba.paw.services.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationDao notificationDao;
    private final MessageSource messageSource;

    @Autowired
    public NotificationServiceImpl(NotificationDao notificationDao, 
                                 MessageSource messageSource) {
        this.notificationDao = notificationDao;
        this.messageSource = messageSource;
    }

    @Transactional 
    @Override
    public void notifyLike(Review review, User likedByUser) {
        String message = messageSource.getMessage(
            "notification.like",
            new Object[]{likedByUser.getUsername(), review.getTitle()},
            LocaleContextHolder.getLocale()
        );

        notificationDao.create(
            Notification.NotificationType.LIKE,
            review.getUser().getId(),
            likedByUser.getId(),
            review.getId(),
            message
        );
    }

    @Transactional
    @Override
    public void notifyComment(Review review, User commentedByUser) {
        String message = messageSource.getMessage(
            "notification.comment",
            new Object[]{commentedByUser.getUsername(), review.getTitle()},
            LocaleContextHolder.getLocale()
        );

        notificationDao.create(
            Notification.NotificationType.COMMENT,
            review.getUser().getId(),
            commentedByUser.getId(),
            review.getId(),
            message
        );
    }

    @Transactional
    @Override
    public void notifyFollow(Long followedUserId, User follower) {
        
        String message = messageSource.getMessage(
            "notification.follow",
            new Object[]{follower.getUsername()},
            LocaleContextHolder.getLocale()
        );

        notificationDao.create(
            Notification.NotificationType.FOLLOW,
            followedUserId,
            follower.getId(),
            null,
            message
        );
    }

    @Transactional
    @Override
    public void notifyNewReview(Review review, User reviewer) {
        List<User> followers = reviewer.getFollowers();
        
        String message = messageSource.getMessage(
            "notification.new.review",
            new Object[]{reviewer.getUsername(), review.getTitle()},
            LocaleContextHolder.getLocale()
        );

        for (User follower : followers) {
            notificationDao.create(
                Notification.NotificationType.NEW_REVIEW,
                follower.getId(),
                reviewer.getId(),
                review.getId(),
                message
            );
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Notification> getUserNotifications(Long userId, int page, int pageSize) {
         List<Notification> notifications = notificationDao.getNotificationsForUser(userId, page, pageSize);
         notifications.forEach(n -> n.setTimeAgo(TimeUtils.formatTimeAgo(n.getCreatedAt())));
         return notifications;
    }

    @Transactional
    @Override
    public void markAsRead(Long notificationId) {
        notificationDao.markAsRead(notificationId);
    }

    @Transactional
    @Override
    public void markAllAsRead(Long userId) {
        notificationDao.markAllAsRead(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public int getUnreadCount(Long userId) {
        return notificationDao.getUnreadCount(userId);
    }
} 