package ar.edu.itba.paw.reviews;

import java.time.LocalDateTime;

public class Review {

    private Long id;
    private Long userId;
    private String title;
    private String description;
    private Integer rating;
    private LocalDateTime createdAt;
    private Integer likes;

    public Review(Long userId, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.createdAt = createdAt;
        this.likes = likes;
    }

    public Review(Long id, Long userId, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.createdAt = createdAt;
        this.likes = likes;
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }
}
