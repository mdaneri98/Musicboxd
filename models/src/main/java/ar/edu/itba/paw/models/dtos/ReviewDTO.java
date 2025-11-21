package ar.edu.itba.paw.models.dtos;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class ReviewDTO {

    private Long id;

    @NotNull(message = "{validation.review.userId.notnull}")
    private Long userId;

    private String username;

    private Long userImageId;

    @NotNull(message = "{validation.review.title.notnull}")
    @Size(min = 1, max = 50, message = "{validation.review.title.size}")
    private String title;

    @NotNull(message = "{validation.review.description.notnull}")
    @Size(min = 1, max = 2000, message = "{validation.review.description.size}")
    private String description;

    @NotNull(message = "{validation.review.rating.notnull}")
    @Min(value = 1, message = "{validation.review.rating.min}")
    @Max(value = 5, message = "{validation.review.rating.max}")
    private Integer rating;

    private LocalDateTime createdAt;

    private Integer likes;

    private Boolean isLiked;

    private Boolean isBlocked;

    private Integer commentAmount;

    private String timeAgo;

    // Campos polimórficos para el item relacionado
    @NotNull(message = "{validation.review.itemType.notnull}")
    private String itemType; 
    @NotNull(message = "{validation.review.itemId.notnull}")
    private Long itemId;
    @NotNull(message = "{validation.review.itemName.notnull}")
    private String itemName;
    @NotNull(message = "{validation.review.itemImageId.notnull}")
    private Long itemImageId;

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

    public Boolean isLiked() {
        if (isLiked == null) {
            return false;
        }
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
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

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
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

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public Long getUserImageId() {
        return userImageId;
    }

    public void setUserImageId(Long userImageId) {
        this.userImageId = userImageId;
    }
}

