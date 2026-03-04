package ar.edu.itba.paw.services;

import ar.edu.itba.paw.domain.user.User;
import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.domain.user.UserRepository;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.persistence.NotificationDao;
import ar.edu.itba.paw.exception.not_found.NotificationNotFoundException;
import ar.edu.itba.paw.exception.not_found.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.mail.MessagingException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationDao notificationDao;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Autowired
    public NotificationServiceImpl(NotificationDao notificationDao, EmailService emailService, UserRepository userRepository){
        this.notificationDao = notificationDao;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public Void notifyLike(Review review, User likedByUser) {
        User targetUser = userRepository.findById(new UserId(review.getUserId()))
            .orElseThrow(() -> new UserNotFoundException(review.getUserId()));
        if (!Objects.equals(targetUser.getId(), likedByUser.getId())) {
            notificationDao.create(
                Notification.NotificationType.LIKE,
                targetUser,
                likedByUser,
                review,
                "notification.like"
            );

            if (targetUser.getLikeNotificationsEnabled()) {
                try {
                    emailService.sendNotificationEmail(
                            Notification.NotificationType.LIKE,
                            targetUser,
                            likedByUser,
                            review.getId(),
                            review.getTitle(),
                            review.getItemName(),
                            review.getItemType().toString(),
                            review.getRating()
                    );
                } catch (MessagingException e) {
                    LOGGER.error("Failed to send like notification email to user: {}", targetUser.getEmail().getValue(), e);
                }
            }
        }
        return null;
    }

    @Transactional
    @Override
    public Void notifyComment(Review review, User commentedByUser) {
        User targetUser = userRepository.findById(new UserId(review.getUserId()))
            .orElseThrow(() -> new UserNotFoundException(review.getUserId()));
        if (!Objects.equals(targetUser.getId(), commentedByUser.getId())) {
            notificationDao.create(
                    Notification.NotificationType.COMMENT,
                    targetUser,
                    commentedByUser,
                    review,
                    "notification.comment"
            );

            if (targetUser.getCommentNotificationsEnabled()) {
                try {
                    emailService.sendNotificationEmail(
                            Notification.NotificationType.COMMENT,
                            targetUser,
                            commentedByUser,
                            review.getId(),
                            review.getTitle(),
                            review.getItemName(),
                            review.getItemType().toString(),
                            review.getRating()
                    );
                } catch (MessagingException e) {
                    LOGGER.error("Failed to send comment notification email to user: {}", targetUser.getEmail().getValue(), e);
                }
            }
        }
        return null;
    }

    @Transactional
    @Override
    public Void notifyFollow(User followedUser, User follower) {
        notificationDao.create(
                Notification.NotificationType.FOLLOW,
                followedUser,
                follower,
                null,
                "notification.follow"
        );

        if (followedUser.getFollowNotificationsEnabled()) {
            try {
                emailService.sendNotificationEmail(
                    Notification.NotificationType.FOLLOW,
                    followedUser,
                    follower,
                    null,
                    null,
                    null,
                    null,
                    null
                );
            } catch (MessagingException e) {
                LOGGER.error("Failed to send follow notification email to user: {}", followedUser.getEmail().getValue(), e);
            }
        }
        return null;
    }

    @Transactional
    @Override
    public Void notifyNewReview(Review review) {
        User reviewer = userRepository.findById(new UserId(review.getUserId()))
            .orElseThrow(() -> new UserNotFoundException(review.getUserId()));
        List<User> followers = reviewer.getFollowers();

        for (User follower : followers) {
            notificationDao.create(
                    Notification.NotificationType.NEW_REVIEW,
                    follower,
                    reviewer,
                    review,
                    "notification.new.review"
            );

            if (follower.getReviewNotificationsEnabled()) {
                try {
                    emailService.sendNotificationEmail(
                        Notification.NotificationType.NEW_REVIEW,
                        follower,
                        reviewer,
                        review.getId(),
                        review.getTitle(),
                        review.getItemName(),
                        review.getItemType().toString(),
                        review.getRating()
                    );
                } catch (MessagingException e) {
                    LOGGER.error("Failed to send new review notification email to user: {}", follower.getEmail().getValue(), e);
                }
            }
        }
        return null;
    }

    @Transactional
    @Override
    public Void notifyReviewBlockStatusChange(Review review, Notification.NotificationType notificationType) {
        User targetUser = userRepository.findById(new UserId(review.getUserId()))
            .orElseThrow(() -> new UserNotFoundException(review.getUserId()));
        String messageKey = "";
        ReviewAcknowledgementType emailType = null;

        if (notificationType.equals(Notification.NotificationType.REVIEW_BLOCKED)) {
            messageKey = "notification.review.blocked";
            emailType = ReviewAcknowledgementType.BLOCKED;
            LOGGER.info("Review {} was blocked, notifying user {}", review.getId(), targetUser.getEmail().getValue());
        } else if (notificationType.equals(Notification.NotificationType.REVIEW_UNBLOCKED)) {
            messageKey = "notification.review.unblocked";
            emailType = ReviewAcknowledgementType.UNBLOCKED;
            LOGGER.info("Review {} was unblocked, notifying user {}", review.getId(), targetUser.getEmail().getValue());
        }

        notificationDao.create(
                notificationType,
                targetUser,
                null,
                review,
                messageKey
        );

        try {
            emailService.sendReviewAcknowledgement(
                    emailType,
                    targetUser,
                    review.getTitle(),
                    review.getItemName(),
                    review.getItemType().toString()
            );
            LOGGER.info("Acknowledgement email sent successfully to user: {}", targetUser.getEmail().getValue());
        } catch (MessagingException e) {
            LOGGER.error("Failed to send acknowledgement email to user: {}", targetUser.getEmail().getValue(), e);
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
    public Long countByUserId(Long userId, StatusType status) {
        switch (status) {
            case READ:
                return notificationDao.countByUserId(userId, true);
            case UNREAD:
                return notificationDao.countByUserId(userId, false);
            case ALL:
                return notificationDao.countByUserId(userId);
            default:
                throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Notification> getUserNotifications(Long userId, Integer page, Integer pageSize, StatusType status) {
        List<Notification> notifications = notificationDao.getNotificationsForUser(userId, page, pageSize);
        switch (status) {
            case READ:
                return notifications.stream().filter(Notification::isRead).collect(Collectors.toList());
            case UNREAD:
                return notifications.stream().filter(n -> !n.isRead()).collect(Collectors.toList());
            case ALL:
                return notifications;
            default:
                throw new IllegalArgumentException("Invalid status: " + status);
        }
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
