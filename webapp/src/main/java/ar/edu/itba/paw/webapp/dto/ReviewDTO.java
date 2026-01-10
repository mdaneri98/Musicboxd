package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.reviews.ReviewType;
import java.time.LocalDateTime;
import java.util.Objects;

public class ReviewDTO {

    private Long id;
    private String username;
    private Long userId;
    private Long userImageId;
    private String title;
    private String description;
    private Integer rating;
    private LocalDateTime createdAt;
    private Integer likes;
    private Boolean isBlocked;
    private Integer commentAmount;
    private ReviewType itemType; 
    private Long itemId;
    private String itemName;
    private Long itemImageId;
    private Boolean userVerified;
    private Boolean userModerator;

    public ReviewDTO() {}

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

    public Boolean getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(Boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public Integer getCommentAmount() {
        return commentAmount;
    }

    public void setCommentAmount(Integer commentAmount) {
        this.commentAmount = commentAmount;
    }

    public ReviewType getItemType() {
        return itemType;
    }

    public void setItemType(ReviewType itemType) {
        this.itemType = itemType;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getItemImageId() {
        return itemImageId;
    }

    public void setItemImageId(Long itemImageId) {
        this.itemImageId = itemImageId;
    }

    public Long getUserImageId() {
        return userImageId;
    }

    public void setUserImageId(Long userImageId) {
        this.userImageId = userImageId;
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

    @Override
    public int hashCode() {
        return Objects.hash(id, username, userId, userImageId, title, description, rating, createdAt, likes, isBlocked, commentAmount, itemType, itemId, itemName, itemImageId, userVerified, userModerator);
    }
}

