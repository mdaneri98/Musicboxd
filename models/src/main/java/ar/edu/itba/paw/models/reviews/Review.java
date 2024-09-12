package ar.edu.itba.paw.models.reviews;

import ar.edu.itba.paw.models.User;

import java.time.LocalDateTime;

public class Review {

    private Long id;
    private User user;
    private String title;
    private String description;
    private Integer rating;
    private LocalDateTime createdAt;
    private Integer likes;

    public Review(User user, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.createdAt = createdAt;
        this.likes = likes;
    }

    public Review(Long id, User user, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes) {
        this.id = id;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
