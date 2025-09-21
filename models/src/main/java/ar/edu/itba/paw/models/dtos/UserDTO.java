package ar.edu.itba.paw.models.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class UserDTO {

    private Long id;
    
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
    @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9]*", message = "Username must start with a letter and contain only letters and numbers")
    private String username;
    
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;
    private Long imageId;

    private Integer followersAmount;
    private Integer followingAmount;
    private Integer reviewsAmount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Boolean isVerified;
    private Boolean isModerator;

    @Size(max = 20, message = "Preferred language must not exceed 20 characters")
    private String preferredLanguage;
    
    @Size(max = 20, message = "Preferred theme must not exceed 20 characters")
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

    public Integer getReviewsAmount() {
        return reviewsAmount;
    }

    public void setReviewsAmount(Integer reviewsAmount) {
        this.reviewsAmount = reviewsAmount;
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
}
