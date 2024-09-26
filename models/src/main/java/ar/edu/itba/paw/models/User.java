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

    public User(){}

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
        this.followersAmount = followersAmount != null ? followersAmount : 0;
        this.followingAmount = followingAmount != null ? followingAmount : 0;
        this.reviewAmount = reviewAmount != null ? reviewAmount : 0;
    }

    public User(String username, String password, String email) {
        this.id = 0L;
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.imgId = 1L;
        this.verified = false;
        this.moderator = false;
        this.followersAmount = 0;
        this.followingAmount = 0;
        this.reviewAmount = 0;
    }

    public User(Long id, String username, String name, Long imgId, Boolean verified, Boolean moderator) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.imgId = imgId;
        this.verified = verified;
        this.moderator = moderator;
    }

    // Getters y setters
    public Integer getReviewAmount() {
        return reviewAmount != null ? reviewAmount : 0;
    }

    public void setReviewAmount(Integer reviewsAmount) {
        this.reviewAmount = reviewsAmount != null ? reviewsAmount : 0;
    }

    public void incrementReviewAmount() {
        this.reviewAmount = (this.reviewAmount != null ? this.reviewAmount : 0) + 1;
    }

    public void decrementReviewAmount() {
        this.reviewAmount = Math.max(0, (this.reviewAmount != null ? this.reviewAmount : 0) - 1);
    }

    public Integer getFollowingAmount() {
        return followingAmount != null ? followingAmount : 0;
    }

    public void setFollowingAmount(Integer followingAmount) {
        this.followingAmount = followingAmount != null ? followingAmount : 0;
    }

    public Integer getFollowersAmount() {
        return followersAmount != null ? followersAmount : 0;
    }

    public void setFollowersAmount(Integer followersAmount) {
        this.followersAmount = followersAmount != null ? followersAmount : 0;
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
        return verified == null ? false : verified;
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

    public static class Builder {
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

        public Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder bio(String bio) {
            this.bio = bio;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder verified(Boolean verified) {
            this.verified = verified;
            return this;
        }

        public Builder moderator(Boolean moderator) {
            this.moderator = moderator;
            return this;
        }

        public Builder followersAmount(Integer followersAmount) {
            this.followersAmount = followersAmount;
            return this;
        }

        public Builder followingAmount(Integer followingAmount) {
            this.followingAmount = followingAmount;
            return this;
        }

        public Builder reviewAmount(Integer reviewAmount) {
            this.reviewAmount = reviewAmount;
            return this;
        }

        public Builder imgId(Long imgId) {
            this.imgId = imgId;
            return this;
        }

        public User build() {
            User user = new User();
            user.id = this.id;
            user.username = this.username;
            user.email = this.email;
            user.password = this.password;
            user.name = this.name;
            user.bio = this.bio;
            user.createdAt = this.createdAt;
            user.updatedAt = this.updatedAt;
            user.verified = this.verified;
            user.moderator = this.moderator;
            user.followersAmount = this.followersAmount;
            user.followingAmount = this.followingAmount;
            user.reviewAmount = this.reviewAmount;
            user.imgId = this.imgId;
            return user;
        }
    }

    // Método para convertir a JSON
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":").append(id).append(",");
        json.append("\"username\":\"").append(username).append("\",");
        json.append("\"email\":\"").append(email).append("\",");
        json.append("\"password\":\"").append(password).append("\",");
        json.append("\"name\":\"").append(name).append("\",");
        json.append("\"bio\":\"").append(bio).append("\",");
        json.append("\"createdAt\":\"").append(createdAt != null ? createdAt.toString() : null).append("\",");
        json.append("\"updatedAt\":\"").append(updatedAt != null ? updatedAt.toString() : null).append("\",");
        json.append("\"verified\":").append(verified).append(",");
        json.append("\"moderator\":").append(moderator).append(",");
        json.append("\"followersAmount\":").append(followersAmount).append(",");
        json.append("\"followingAmount\":").append(followingAmount).append(",");
        json.append("\"reviewAmount\":").append(reviewAmount).append(",");
        json.append("\"imgId\":").append(imgId);
        json.append("}");
        return json.toString();
    }

}
