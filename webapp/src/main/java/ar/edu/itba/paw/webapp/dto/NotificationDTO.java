package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.webapp.dto.links.NotificationLinksDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class NotificationDTO {

    private Long id;
    private String type;
    private Long recipientUserId;
    private String recipientUsername;
    private Long triggerUserId;
    private String triggerUsername;
    private Long triggerUserImageId;
    private Long reviewId;
    private String reviewItemName;
    private Long reviewItemImageId;
    private LocalDateTime createdAt;
    private Boolean isRead;
    private String message;

    @JsonProperty("_links")
    private NotificationLinksDTO links;

    public NotificationDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (!type.equals(Notification.NotificationType.LIKE.name())
                && !type.equals(Notification.NotificationType.COMMENT.name())
                && !type.equals(Notification.NotificationType.FOLLOW.name())
                && !type.equals(Notification.NotificationType.NEW_REVIEW.name())
                && !type.equals(Notification.NotificationType.REVIEW_BLOCKED.name())
                && !type.equals(Notification.NotificationType.REVIEW_UNBLOCKED.name())) {
            throw new IllegalArgumentException("Invalid notification type");
        }
        this.type = type;
    }

    public Long getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(Long recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    public String getRecipientUsername() {
        return recipientUsername;
    }

    public void setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
    }

    public Long getTriggerUserId() {
        return triggerUserId;
    }

    public void setTriggerUserId(Long triggerUserId) {
        this.triggerUserId = triggerUserId;
    }

    public String getTriggerUsername() {
        return triggerUsername;
    }

    public void setTriggerUsername(String triggerUsername) {
        this.triggerUsername = triggerUsername;
    }

    public Long getTriggerUserImageId() {
        return triggerUserImageId;
    }

    public void setTriggerUserImageId(Long triggerUserImageId) {
        this.triggerUserImageId = triggerUserImageId;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public String getReviewItemName() {
        return reviewItemName;
    }

    public void setReviewItemName(String reviewItemName) {
        this.reviewItemName = reviewItemName;
    }

    public Long getReviewItemImageId() {
        return reviewItemImageId;
    }

    public void setReviewItemImageId(Long reviewItemImageId) {
        this.reviewItemImageId = reviewItemImageId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // HATEOAS links getter and setter
    public NotificationLinksDTO getLinks() {
        return links;
    }

    public void setLinks(NotificationLinksDTO links) {
        this.links = links;
    }
}
