package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.persistence.NotificationDao;
import ar.edu.itba.paw.services.utils.TimeUtils;
import ar.edu.itba.paw.exception.not_found.NotificationNotFoundException;
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
    public Void notifyNewReview(Review review) {
        User reviewer = review.getUser();
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
    public Notification findById(Long id) {
        return notificationDao.findById(id).orElseThrow(() -> new NotificationNotFoundException(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Notification> findAll() {
        return notificationDao.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Notification> findPaginated(FilterType filterType, Integer page, Integer pageSize) {
        return notificationDao.findPaginated(filterType, page, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Notification> findBySubstring(String substring, Integer page, Integer pageSize) {
        return notificationDao.findBySubstring(substring, page, pageSize);
    }

    @Transactional
    @Override
    public Notification create(Notification notificationInput) {
        return notificationDao.create(notificationInput);
    }

    @Transactional
    @Override
    public Notification update(Notification notificationInput) {
        Notification notification = notificationDao.findById(notificationInput.getId())
                .orElseThrow(() -> new NotificationNotFoundException(notificationInput.getId()));
        
        if (notificationInput.getMessage() != null) {
            notification.setMessage(notificationInput.getMessage());
        }
        notification.setRead(notificationInput.isRead());
        
        return notificationDao.update(notification);
    }

    @Transactional
    @Override
    public Boolean delete(Long id) {
        notificationDao.delete(id);
        return true;
    }

    @Transactional(readOnly = true)
    @Override
    public Long countAll() {
        return notificationDao.countAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Long countByUserId(Long userId) {
        return notificationDao.countByUserId(userId);
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
