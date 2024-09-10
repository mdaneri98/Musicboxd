package ar.edu.itba.paw.models;


import java.time.LocalDateTime;

public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String name;
    private String bio;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean verified;
    private Boolean moderator;
    private Integer followersAmount;
    private Integer followingAmount;
    private Integer reviewAmount;
    private Long imgId;

    public User(Long id, String username, String email, String password, String name, String bio, LocalDateTime createdAt, LocalDateTime updatedAt, boolean verified, Long imgId, Boolean moderator, Integer followersAmount, Integer followingAmount, Integer reviewAmount) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
        this.bio = bio;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.verified = verified;
        this.imgId = imgId;
        this.moderator = moderator;
        this.followersAmount = followersAmount;
        this.followingAmount = followingAmount;
        this.reviewAmount = reviewAmount;
    }

    public User(String username, String password, String email) {
        this.id = 0L;
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.imgId = 1L;
    }

    public static User unverifiedUser(String email) {
        return new User(
            0L,
                "",
                email,
                "",
                "",
                "",
                LocalDateTime.now(),
                LocalDateTime.now(),
                false,
                1L,
                false,
                0,
                0,
                0
        );
    }

    // Getters y setters

    public Integer getReviewAmount() {
        return reviewAmount;
    }

    public void setReviewAmount(Integer reviewsAmount) {
        this.reviewAmount = reviewsAmount;
    }

    public void incrementReviewAmount() {
        this.reviewAmount++;
    }

    public void decrementReviewAmount() {
        this.reviewAmount--;
    }

    public Integer getFollowingAmount() {
        return followingAmount;
    }

    public void setFollowingAmount(Integer followingAmount) {
        this.followingAmount = followingAmount;
    }

    public Integer getFollowersAmount() {
        return followersAmount;
    }

    public void setFollowersAmount(Integer followersAmount) {
        this.followersAmount = followersAmount;
    }

    public boolean isModerator() {
        return moderator;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public Long getImgId() {
        return imgId;
    }

    public void setImgId(Long imgId) {
        this.imgId = imgId;
    }
}
