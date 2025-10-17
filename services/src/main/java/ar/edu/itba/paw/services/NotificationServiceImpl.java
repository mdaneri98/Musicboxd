package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.dtos.NotificationDTO;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.persistence.NotificationDao;
import ar.edu.itba.paw.services.mappers.NotificationMapper;
import ar.edu.itba.paw.services.utils.TimeUtils;
import ar.edu.itba.paw.services.utils.MergeUtils;
import ar.edu.itba.paw.services.exception.NotificationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationDao notificationDao;
    private final NotificationMapper notificationMapper;

    @Autowired
    public NotificationServiceImpl(NotificationDao notificationDao, NotificationMapper notificationMapper){
        this.notificationDao = notificationDao;
        this.notificationMapper = notificationMapper;
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
    public NotificationDTO findById(Long id) {
        Notification notification = notificationDao.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification with id " + id + " not found"));
        return notificationMapper.toDTO(notification);
    }

    @Transactional(readOnly = true)
    @Override
    public List<NotificationDTO> findAll() {
        List<Notification> notifications = notificationDao.findAll();
        return notificationMapper.toDTOList(notifications);
    }

    @Transactional(readOnly = true)
    @Override
    public List<NotificationDTO> findPaginated(FilterType filterType, Integer page, Integer pageSize) {
        List<Notification> notifications = notificationDao.findPaginated(filterType, page, pageSize);
        return notificationMapper.toDTOList(notifications);
    }

    @Transactional(readOnly = true)
    @Override
    public List<NotificationDTO> findBySubstring(String substring, Integer page, Integer pageSize) {
        List<Notification> notifications = notificationDao.findBySubstring(substring, page, pageSize);
        return notificationMapper.toDTOList(notifications);
    }

    @Transactional
    @Override
    public NotificationDTO create(NotificationDTO notificationDTO) {
        Notification notification = notificationMapper.toEntity(notificationDTO);
        Notification savedNotification = notificationDao.create(notification);
        return notificationMapper.toDTO(savedNotification);
    }

    @Transactional
    @Override
    public NotificationDTO update(NotificationDTO notificationDTO) {
        Notification notification = notificationDao.findById(notificationDTO.getId()).orElseThrow(() -> new NotificationNotFoundException("Notification with id " + notificationDTO.getId() + " not found"));
        MergeUtils.mergeNotificationFields(notification, notificationDTO);
        Notification updatedNotification = notificationDao.update(notification);
        return notificationMapper.toDTO(updatedNotification);
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
    public List<NotificationDTO> getUserNotifications(Long userId, Integer page, Integer pageSize) {
         List<Notification> notifications = notificationDao.getNotificationsForUser(userId, page, pageSize);
         notifications.forEach(n -> n.setTimeAgo(TimeUtils.formatTimeAgo(n.getCreatedAt())));
         return notificationMapper.toDTOList(notifications);
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