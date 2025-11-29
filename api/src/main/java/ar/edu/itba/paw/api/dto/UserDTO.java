package ar.edu.itba.paw.api.dto;

import java.time.LocalDateTime;

public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String name;
    private String bio;
    private Long imageId;
    private Integer followersAmount;
    private Integer followingAmount;
    private Integer reviewAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isVerified;
    private Boolean isModerator;
    private Boolean isFollowed;
    private String preferredLanguage;
    private String preferredTheme;
    private Boolean hasFollowNotificationsEnabled;
    private Boolean hasLikeNotificationsEnabled;
    private Boolean hasCommentsNotificationsEnabled;
    private Boolean hasReviewsNotificationsEnabled;

    public UserDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public Integer getFollowersAmount() {
        return followersAmount;
    }

    public void setFollowersAmount(Integer followersAmount) {
        this.followersAmount = followersAmount;
    }

    public Integer getFollowingAmount() {
        return followingAmount;
    }

    public void setFollowingAmount(Integer followingAmount) {
        this.followingAmount = followingAmount;
    }

    public Integer getReviewAmount() {
        return reviewAmount;
    }

    public void setReviewAmount(Integer reviewsAmount) {
        this.reviewAmount = reviewsAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getVerified() {
        return isVerified;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
    }

    public Boolean getModerator() {
        return isModerator;
    }

    public void setModerator(Boolean moderator) {
        isModerator = moderator;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getPreferredTheme() {
        return preferredTheme;
    }

    public void setPreferredTheme(String preferredTheme) {
        this.preferredTheme = preferredTheme;
    }

    public Boolean getHasFollowNotificationsEnabled() {
        return hasFollowNotificationsEnabled;
    }

    public void setHasFollowNotificationsEnabled(Boolean hasFollowNotificationsEnabled) {
        this.hasFollowNotificationsEnabled = hasFollowNotificationsEnabled;
    }

    public Boolean getHasLikeNotificationsEnabled() {
        return hasLikeNotificationsEnabled;
    }

    public void setHasLikeNotificationsEnabled(Boolean hasLikeNotificationsEnabled) {
        this.hasLikeNotificationsEnabled = hasLikeNotificationsEnabled;
    }

    public Boolean getHasCommentsNotificationsEnabled() {
        return hasCommentsNotificationsEnabled;
    }

    public void setHasCommentsNotificationsEnabled(Boolean hasCommentsNotificationsEnabled) {
        this.hasCommentsNotificationsEnabled = hasCommentsNotificationsEnabled;
    }

    public Boolean getHasReviewsNotificationsEnabled() {
        return hasReviewsNotificationsEnabled;
    }

    public void setHasReviewsNotificationsEnabled(Boolean hasReviewsNotificationsEnabled) {
        this.hasReviewsNotificationsEnabled = hasReviewsNotificationsEnabled;
    }

    public Boolean isFollowed() {
        if (isFollowed == null) {
            return false;
        }
        return isFollowed;
    }

    public void setFollowed(Boolean isFollowed) {
        this.isFollowed = isFollowed;
    }
}

