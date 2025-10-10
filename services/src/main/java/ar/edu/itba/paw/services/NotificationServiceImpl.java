package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.NotificationDao;
import ar.edu.itba.paw.services.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationDao notificationDao;

    @Autowired
    public NotificationServiceImpl(NotificationDao notificationDao){
        this.notificationDao = notificationDao;
    }

    @Transactional 
    @Override
    public Void notifyLike(Review review, User likedByUser) {
        User targetUser = review.getUser();
        if (targetUser.getId() != likedByUser.getId() && targetUser.getLikeNotificationsEnabled()) {
            notificationDao.create(
                Notification.NotificationType.LIKE,
                targetUser,
                likedByUser,
                review,
                "notification.like"
            );
        }
        return null;
    }

    @Transactional
    @Override
    public Void notifyComment(Review review, User commentedByUser) {
        User targetUser = review.getUser();
        if (targetUser.getId() != commentedByUser.getId() && targetUser.getCommentNotificationsEnabled()) {
            notificationDao.create(
                Notification.NotificationType.COMMENT,
                targetUser,
                commentedByUser,
                review,
                "notification.comment"  
            );
        }
        return null;
    }

    @Transactional
    @Override
    public Void notifyFollow(User followedUser, User follower) {
        if (followedUser.getFollowNotificationsEnabled()) {
            notificationDao.create(
                Notification.NotificationType.FOLLOW,
                followedUser,
                follower,
                null,
                "notification.follow"
            );
        }
        return null;
    }

    @Transactional
    @Override
    public Void notifyNewReview(Review review, User reviewer) {
        List<User> followers = reviewer.getFollowers();

        for (User follower : followers) {
            if (follower.getReviewNotificationsEnabled()) {
                notificationDao.create(
                    Notification.NotificationType.NEW_REVIEW,
                    follower,
                    reviewer,
                    review,
                    "notification.new.review"
                );
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Notification> getUserNotifications(Long userId, Integer page, Integer pageSize) {
         List<Notification> notifications = notificationDao.getNotificationsForUser(userId, page, pageSize);
         notifications.forEach(n -> n.setTimeAgo(TimeUtils.formatTimeAgo(n.getCreatedAt())));
         return notifications;
    }

    @Transactional
    @Override
    public Void markAsRead(Long notificationId) {
        notificationDao.markAsRead(notificationId);
        return null;
    }

    @Transactional
    @Override
    public Void markAllAsRead(Long userId) {
        notificationDao.markAllAsRead(userId);
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public Integer getUnreadCount(Long userId) {
        return notificationDao.getUnreadCount(userId);
    }
} 