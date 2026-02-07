package ar.edu.itba.paw.webapp.dto;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Objects;


public class UserDTO {

    private Long id;
    private String username;
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
    private String preferredLanguage;
    private String preferredTheme;
    private Boolean hasFollowNotificationsEnabled;
    private Boolean hasLikeNotificationsEnabled;
    private Boolean hasCommentsNotificationsEnabled;
    private Boolean hasReviewsNotificationsEnabled;

    // HATEOAS links
    private URI self;
    private URI image;
    private URI reviews;
    private URI followers;
    private URI following;
    private URI favoriteArtists;
    private URI favoriteAlbums;
    private URI favoriteSongs;

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

    // HATEOAS getters and setters
    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getImage() {
        return image;
    }

    public void setImage(URI image) {
        this.image = image;
    }

    public URI getReviews() {
        return reviews;
    }

    public void setReviews(URI reviews) {
        this.reviews = reviews;
    }

    public URI getFollowers() {
        return followers;
    }

    public void setFollowers(URI followers) {
        this.followers = followers;
    }

    public URI getFollowing() {
        return following;
    }

    public void setFollowing(URI following) {
        this.following = following;
    }

    public URI getFavoriteArtists() {
        return favoriteArtists;
    }

    public void setFavoriteArtists(URI favoriteArtists) {
        this.favoriteArtists = favoriteArtists;
    }

    public URI getFavoriteAlbums() {
        return favoriteAlbums;
    }

    public void setFavoriteAlbums(URI favoriteAlbums) {
        this.favoriteAlbums = favoriteAlbums;
    }

    public URI getFavoriteSongs() {
        return favoriteSongs;
    }

    public void setFavoriteSongs(URI favoriteSongs) {
        this.favoriteSongs = favoriteSongs;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, name, bio, imageId, followersAmount, followingAmount, reviewAmount, createdAt, updatedAt, isVerified, isModerator, preferredLanguage, preferredTheme, hasFollowNotificationsEnabled, hasLikeNotificationsEnabled, hasCommentsNotificationsEnabled, hasReviewsNotificationsEnabled);
    }
}

