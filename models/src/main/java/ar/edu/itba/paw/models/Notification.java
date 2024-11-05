package ar.edu.itba.paw.models;

import javax.persistence.*;
import ar.edu.itba.paw.models.reviews.Review;
import java.time.LocalDateTime;


@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipient_user_id", nullable = false)
    private User recipientUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trigger_user_id")
    private User triggerUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resource_id")
    private Review review;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "read", nullable = false)
    private boolean read;

    @Column(name = "message", nullable = false)
    private String message;

    @Transient
    private String timeAgo;
    

    public enum NotificationType {
        LIKE,
        COMMENT,
        FOLLOW,
        NEW_REVIEW
    }

    public Notification() {
    }

    public Notification(Long id, NotificationType type, User recipientUser,
                       User triggerUser, Review review, LocalDateTime createdAt, 
                       boolean read, String message) {
        this.id = id;
        this.type = type;
        this.recipientUser = recipientUser;
        this.triggerUser = triggerUser;
        this.review = review;
        this.createdAt = createdAt;
        this.read = read;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public User getRecipientUser() {
        return recipientUser;
    }

    public void setRecipientUser(User recipientUser) {
        this.recipientUser = recipientUser;
    }

    public User getTriggerUser() {
        return triggerUser;
    }

    public void setTriggerUser(User triggerUser) {
        this.triggerUser = triggerUser;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }
}
