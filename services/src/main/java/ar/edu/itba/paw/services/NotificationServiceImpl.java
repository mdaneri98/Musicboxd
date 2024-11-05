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
    public void notifyLike(Review review, User likedByUser) {
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
    }

    @Transactional
    @Override
    public void notifyComment(Review review, User commentedByUser) {
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
    }

    @Transactional
    @Override
    public void notifyFollow(User followedUser, User follower) {
        if (followedUser.getFollowNotificationsEnabled()) {
            notificationDao.create(
                Notification.NotificationType.FOLLOW,
                followedUser,
                follower,
                null,
                "notification.follow"
            );
        }
    }

    @Transactional
    @Override
    public void notifyNewReview(Review review, User reviewer) {
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