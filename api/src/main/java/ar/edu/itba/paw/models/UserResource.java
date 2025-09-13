package ar.edu.itba.paw.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * HATEOAS resource wrapper for User entities
 */
public class UserResource extends Resource<User> {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("bio")
    private String bio;
    
    @JsonProperty("verified")
    private Boolean verified;
    
    @JsonProperty("moderator")
    private Boolean moderator;
    
    @JsonProperty("followersAmount")
    private Integer followersAmount;
    
    @JsonProperty("followingAmount")
    private Integer followingAmount;
    
    @JsonProperty("reviewAmount")
    private Integer reviewAmount;
    
    @JsonProperty("imageId")
    private Long imageId;
    
    @JsonProperty("createdAt")
    private String createdAt;
    
    @JsonProperty("updatedAt")
    private String updatedAt;
    
    public UserResource() {}
    
    public UserResource(User user) {
        if (user != null) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.name = user.getName();
            this.bio = user.getBio();
            this.verified = user.getVerified();
            this.moderator = user.getModerator();
            this.followersAmount = user.getFollowersAmount();
            this.followingAmount = user.getFollowingAmount();
            this.reviewAmount = user.getReviewAmount();
            this.imageId = user.getImage() != null ? user.getImage().getId() : null;
            this.createdAt = user.getCreatedAt() != null ? user.getCreatedAt().toString() : null;
            this.updatedAt = user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null;
        }
    }
    
    @Override
    public User getData() {
        // Return a minimal User object with just the ID for reference
        User user = new User();
        user.setId(this.id);
        return user;
    }
    
    // Getters and setters
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
    
    public Boolean getVerified() {
        return verified;
    }
    
    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
    
    public Boolean getModerator() {
        return moderator;
    }
    
    public void setModerator(Boolean moderator) {
        this.moderator = moderator;
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
    
    public void setReviewAmount(Integer reviewAmount) {
        this.reviewAmount = reviewAmount;
    }
    
    public Long getImageId() {
        return imageId;
    }
    
    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
