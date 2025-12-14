package ar.edu.itba.paw.webapp.dto;

import java.time.LocalDateTime;

public class CommentDTO {

    private Long id;
    private Long userId;
    private String username;
    private Long userImageId;
    private Long reviewId;
    private String content;
    private LocalDateTime createdAt;
    private Boolean userVerified;
    private Boolean userModerator;

    public CommentDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserImageId() {
        return userImageId;
    }

    public void setUserImageId(Long userImageId) {
        this.userImageId = userImageId;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getUserVerified() {
        return userVerified;
    }

    public void setUserVerified(Boolean userVerified) {
        this.userVerified = userVerified;
    }

    public Boolean getUserModerator() {
        return userModerator;
    }

    public void setUserModerator(Boolean userModerator) {
        this.userModerator = userModerator;
    }
}

