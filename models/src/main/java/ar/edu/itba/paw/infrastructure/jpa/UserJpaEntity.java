package ar.edu.itba.paw.infrastructure.jpa;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cuser")
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cuser_id_seq")
    @SequenceGenerator(sequenceName = "cuser_id_seq", name = "cuser_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean verified;

    @Column(name = "ismoderator")
    private Boolean isModerator;

    @Column(name = "review_amount")
    private Integer reviewAmount;

    @Column(name = "follower_amount")
    private Integer followerAmount;

    @Column(name = "following_amount")
    private Integer followingAmount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "preferred_language", length = 10)
    private String preferredLanguage;

    @Column(name = "theme", length = 20)
    private String theme;

    @Column(name = "follow_notifications_enabled")
    private Boolean followNotificationsEnabled;

    @Column(name = "like_notifications_enabled")
    private Boolean likeNotificationsEnabled;

    @Column(name = "comment_notifications_enabled")
    private Boolean commentNotificationsEnabled;

    @Column(name = "review_notifications_enabled")
    private Boolean reviewNotificationsEnabled;

    @ElementCollection
    @CollectionTable(name = "favorite_album", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "album_id")
    private List<Long> favoriteAlbumIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "favorite_artist", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "artist_id")
    private List<Long> favoriteArtistIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "favorite_song", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "song_id")
    private List<Long> favoriteSongIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "follow", joinColumns = @JoinColumn(name = "follower_id"))
    @Column(name = "followed_id")
    private List<Long> followingIds = new ArrayList<>();

    public UserJpaEntity() {}

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

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getIsModerator() {
        return isModerator;
    }

    public void setIsModerator(Boolean isModerator) {
        this.isModerator = isModerator;
    }

    public Integer getReviewAmount() {
        return reviewAmount;
    }

    public void setReviewAmount(Integer reviewAmount) {
        this.reviewAmount = reviewAmount;
    }

    public Integer getFollowerAmount() {
        return followerAmount;
    }

    public void setFollowerAmount(Integer followerAmount) {
        this.followerAmount = followerAmount;
    }

    public Integer getFollowingAmount() {
        return followingAmount;
    }

    public void setFollowingAmount(Integer followingAmount) {
        this.followingAmount = followingAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Long> getFavoriteAlbumIds() {
        return favoriteAlbumIds;
    }

    public void setFavoriteAlbumIds(List<Long> favoriteAlbumIds) {
        this.favoriteAlbumIds = favoriteAlbumIds;
    }

    public List<Long> getFavoriteArtistIds() {
        return favoriteArtistIds;
    }

    public void setFavoriteArtistIds(List<Long> favoriteArtistIds) {
        this.favoriteArtistIds = favoriteArtistIds;
    }

    public List<Long> getFavoriteSongIds() {
        return favoriteSongIds;
    }

    public void setFavoriteSongIds(List<Long> favoriteSongIds) {
        this.favoriteSongIds = favoriteSongIds;
    }

    public List<Long> getFollowingIds() {
        return followingIds;
    }

    public void setFollowingIds(List<Long> followingIds) {
        this.followingIds = followingIds;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Boolean getFollowNotificationsEnabled() {
        return followNotificationsEnabled;
    }

    public void setFollowNotificationsEnabled(Boolean followNotificationsEnabled) {
        this.followNotificationsEnabled = followNotificationsEnabled;
    }

    public Boolean getLikeNotificationsEnabled() {
        return likeNotificationsEnabled;
    }

    public void setLikeNotificationsEnabled(Boolean likeNotificationsEnabled) {
        this.likeNotificationsEnabled = likeNotificationsEnabled;
    }

    public Boolean getCommentNotificationsEnabled() {
        return commentNotificationsEnabled;
    }

    public void setCommentNotificationsEnabled(Boolean commentNotificationsEnabled) {
        this.commentNotificationsEnabled = commentNotificationsEnabled;
    }

    public Boolean getReviewNotificationsEnabled() {
        return reviewNotificationsEnabled;
    }

    public void setReviewNotificationsEnabled(Boolean reviewNotificationsEnabled) {
        this.reviewNotificationsEnabled = reviewNotificationsEnabled;
    }
}
