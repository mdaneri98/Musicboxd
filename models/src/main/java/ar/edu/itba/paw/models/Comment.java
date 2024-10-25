package ar.edu.itba.paw.models;

import java.time.LocalDateTime;

public class Comment {
    private Long id;
    private User user;
    private Long reviewId;
    private String content;
    private LocalDateTime createdAt;
    private String timeAgo;

    public Comment(Long id, User user, Long reviewId, String content, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.reviewId = reviewId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Comment(User user, Long reviewId, String content) {
        this.user = user;
        this.reviewId = reviewId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }


    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }
}
