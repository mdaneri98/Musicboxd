package ar.edu.itba.paw.domain.user;

import ar.edu.itba.paw.domain.events.DomainEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User {
    private final UserId id;
    private String username;
    private Email email;
    private String password;
    private boolean verified;
    private boolean isModerator;
    private int reviewAmount;
    private int followerAmount;
    private int followingAmount;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Profile fields
    private String name;
    private String bio;
    private Long imageId;

    // Preferences
    private String preferredLanguage;
    private String theme;

    // Notification settings
    private boolean followNotificationsEnabled;
    private boolean likeNotificationsEnabled;
    private boolean commentNotificationsEnabled;
    private boolean reviewNotificationsEnabled;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private User(UserId id, String username, Email email, String password,
                 boolean verified, boolean isModerator, int reviewAmount,
                 int followerAmount, int followingAmount, LocalDateTime createdAt,
                 LocalDateTime updatedAt, String name, String bio, Long imageId,
                 String preferredLanguage, String theme,
                 boolean followNotificationsEnabled, boolean likeNotificationsEnabled,
                 boolean commentNotificationsEnabled, boolean reviewNotificationsEnabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.verified = verified;
        this.isModerator = isModerator;
        this.reviewAmount = reviewAmount;
        this.followerAmount = followerAmount;
        this.followingAmount = followingAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.name = name;
        this.bio = bio;
        this.imageId = imageId;
        this.preferredLanguage = preferredLanguage;
        this.theme = theme;
        this.followNotificationsEnabled = followNotificationsEnabled;
        this.likeNotificationsEnabled = likeNotificationsEnabled;
        this.commentNotificationsEnabled = commentNotificationsEnabled;
        this.reviewNotificationsEnabled = reviewNotificationsEnabled;
    }

    public static User create(String username, Email email, String hashedPassword) {
        validateUsername(username);
        LocalDateTime now = LocalDateTime.now();
        return new User(null, username, email, hashedPassword, false, false,
                       0, 0, 0, now, now, username, null, null,
                       "en", "light", true, true, true, true);
    }

    public static User reconstitute(UserId id, String username, Email email,
                                   String password, boolean verified,
                                   boolean isModerator, int reviewAmount,
                                   int followerAmount, int followingAmount,
                                   LocalDateTime createdAt, LocalDateTime updatedAt,
                                   String name, String bio, Long imageId,
                                   String preferredLanguage, String theme,
                                   boolean followNotificationsEnabled, boolean likeNotificationsEnabled,
                                   boolean commentNotificationsEnabled, boolean reviewNotificationsEnabled) {
        if (id == null) {
            throw new IllegalArgumentException("ID required for reconstitution");
        }
        return new User(id, username, email, password, verified, isModerator,
                       reviewAmount, followerAmount, followingAmount, createdAt,
                       updatedAt != null ? updatedAt : createdAt,
                       name, bio, imageId, preferredLanguage, theme,
                       followNotificationsEnabled, likeNotificationsEnabled,
                       commentNotificationsEnabled, reviewNotificationsEnabled);
    }

    public void verify() {
        if (this.verified) {
            throw new IllegalStateException("User already verified");
        }
        this.verified = true;
    }

    public void promoteToModerator() {
        if (this.isModerator) {
            throw new IllegalStateException("User already moderator");
        }
        this.isModerator = true;
    }

    public void incrementReviewCount() {
        this.reviewAmount++;
    }

    public void decrementReviewCount() {
        if (this.reviewAmount > 0) {
            this.reviewAmount--;
        }
    }

    public void incrementFollowerCount() {
        this.followerAmount++;
    }

    public void decrementFollowerCount() {
        if (this.followerAmount > 0) {
            this.followerAmount--;
        }
    }

    public void incrementFollowingCount() {
        this.followingAmount++;
    }

    public void decrementFollowingCount() {
        if (this.followingAmount > 0) {
            this.followingAmount--;
        }
    }

    public void changePassword(String newHashedPassword) {
        if (newHashedPassword == null || newHashedPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
        this.password = newHashedPassword;
    }

    private static void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("Username must be 3-50 characters");
        }
    }

    protected void addEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    public List<DomainEvent> getEvents() {
        return List.copyOf(domainEvents);
    }

    public void clearEvents() {
        domainEvents.clear();
    }

    public UserId getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Email getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isVerified() {
        return verified;
    }

    public boolean isModerator() {
        return isModerator;
    }

    public int getReviewAmount() {
        return reviewAmount;
    }

    public int getFollowerAmount() {
        return followerAmount;
    }

    public int getFollowingAmount() {
        return followingAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public Long getImageId() {
        return imageId;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public String getTheme() {
        return theme;
    }

    public boolean getFollowNotificationsEnabled() {
        return followNotificationsEnabled;
    }

    public boolean getLikeNotificationsEnabled() {
        return likeNotificationsEnabled;
    }

    public boolean getCommentNotificationsEnabled() {
        return commentNotificationsEnabled;
    }

    public boolean getReviewNotificationsEnabled() {
        return reviewNotificationsEnabled;
    }

    public Integer getFollowersAmount() {
        return followerAmount;
    }

    // Setters for profile fields
    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public void setBio(String bio) {
        this.bio = bio;
        this.updatedAt = LocalDateTime.now();
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
        this.updatedAt = LocalDateTime.now();
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
        this.updatedAt = LocalDateTime.now();
    }

    public void setTheme(String theme) {
        this.theme = theme;
        this.updatedAt = LocalDateTime.now();
    }

    public void setFollowNotificationsEnabled(boolean enabled) {
        this.followNotificationsEnabled = enabled;
        this.updatedAt = LocalDateTime.now();
    }

    public void setLikeNotificationsEnabled(boolean enabled) {
        this.likeNotificationsEnabled = enabled;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCommentNotificationsEnabled(boolean enabled) {
        this.commentNotificationsEnabled = enabled;
        this.updatedAt = LocalDateTime.now();
    }

    public void setReviewNotificationsEnabled(boolean enabled) {
        this.reviewNotificationsEnabled = enabled;
        this.updatedAt = LocalDateTime.now();
    }

    // TODO: Followers - stub method
    // This should query UserRepository.getFollowerIds() and map to User objects
    public List<User> getFollowers() {
        return List.of();
    }
}
