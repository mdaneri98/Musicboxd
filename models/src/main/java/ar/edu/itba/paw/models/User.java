package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cuser")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cuser_id_seq")
    @SequenceGenerator(sequenceName = "cuser_id_seq", name = "cuser_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;

    @Column(name = "email", length = 100, unique = true, nullable = false)
    private String email;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "bio")
    private String bio;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "verified", nullable = false)
    private Boolean verified;

    @Column(name = "moderator", nullable = false)
    private Boolean moderator;

    @Column(name = "followers_amount")
    private Integer followersAmount;

    @Column(name = "following_amount")
    private Integer followingAmount;

    @Column(name = "review_amount")
    private Integer reviewAmount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "img_id", referencedColumnName = "id", nullable = false)
    private Image image;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> comments;

    @ManyToMany
    @JoinTable(
            name = "follower",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "following")
    )
    private List<User> following;

    @ManyToMany(mappedBy = "following")
    private List<User> followers;

    @ManyToMany
    @JoinTable(
            name = "favorite_artist",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private List<Artist> favoriteArtists;

    @ManyToMany
    @JoinTable(
            name = "favorite_album",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "album_id")
    )
    private List<Album> favoriteAlbums;

    @ManyToMany
    @JoinTable(
            name = "favorite_song",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    private List<Song> favoriteSongs;


    public User(){}

    public User(Long id, String username, String email, String password, String name, String bio, LocalDateTime createdAt, LocalDateTime updatedAt, boolean verified, Image image, Boolean moderator, Integer followersAmount, Integer followingAmount, Integer reviewAmount) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
        this.bio = bio;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.verified = verified;
        this.image = image;
        this.moderator = moderator;
        this.followersAmount = followersAmount != null ? followersAmount : 0;
        this.followingAmount = followingAmount != null ? followingAmount : 0;
        this.reviewAmount = reviewAmount != null ? reviewAmount : 0;
    }

    public User(Long id, String username, String email, String password, String name, String bio, boolean verified, Image image, Boolean moderator, Integer followersAmount, Integer followingAmount, Integer reviewAmount) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
        this.bio = bio;
        this.verified = verified;
        this.image = image;
        this.moderator = moderator;
        this.followersAmount = followersAmount != null ? followersAmount : 0;
        this.followingAmount = followingAmount != null ? followingAmount : 0;
        this.reviewAmount = reviewAmount != null ? reviewAmount : 0;
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.image = null;
        this.verified = false;
        this.moderator = false;
        this.followersAmount = 0;
        this.followingAmount = 0;
        this.reviewAmount = 0;
    }

    public User(Long id, String username, String name, Image image, Boolean verified, Boolean moderator) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.image = image;
        this.verified = verified;
        this.moderator = moderator;
    }

    public User(Long id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public static User createAnonymous() {
        User anonymousUser = new User();
        anonymousUser.id = 0L;
        anonymousUser.username = "";
        anonymousUser.email = "";
        anonymousUser.password = "";
        anonymousUser.name = "";
        anonymousUser.bio = "";
        anonymousUser.createdAt = LocalDateTime.now();
        anonymousUser.updatedAt = LocalDateTime.now();
        anonymousUser.verified = false;
        anonymousUser.moderator = false;
        anonymousUser.followersAmount = 0;
        anonymousUser.followingAmount = 0;
        anonymousUser.reviewAmount = 0;
        anonymousUser.image = null;

        return anonymousUser;
    }

    public static boolean isAnonymus(User user) {
        return user == null || user.getId() <= 0;
    }

    // Implementaciones concretas
    public void addFollowing(User user) {
        if (this.following == null) {
            this.following = new ArrayList<>();
        }
        if (!this.following.contains(user)) {
            this.following.add(user);
            user.getFollowers().add(this); // Añadir a la lista de seguidores del otro usuario
            this.followingAmount++;
            user.setFollowersAmount(user.getFollowersAmount() + 1);
        }
    }

    public void removeFollowing(User user) {
        if (this.following != null && this.following.contains(user)) {
            this.following.remove(user);
            user.getFollowers().remove(this); // Remover de la lista de seguidores del otro usuario
            this.followingAmount--;
            user.setFollowersAmount(user.getFollowersAmount() - 1);
        }
    }

    public void addFollower(User user) {
        if (this.followers == null) {
            this.followers = new ArrayList<>();
        }
        if (!this.followers.contains(user)) {
            this.followers.add(user);
            user.getFollowing().add(this); // Añadir a la lista de "following" del otro usuario
            this.followersAmount++;
            user.setFollowingAmount(user.getFollowingAmount() + 1);
        }
    }

    public void removeFollower(User user) {
        if (this.followers != null && this.followers.contains(user)) {
            this.followers.remove(user);
            user.getFollowing().remove(this); // Remover de la lista de "following" del otro usuario
            this.followersAmount--;
            user.setFollowingAmount(user.getFollowingAmount() - 1);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    // Getters y setters
    public List<Artist> getFavoriteArtists() {
        return favoriteArtists;
    }

    public List<Album> getFavoriteAlbums() {
        return favoriteAlbums;
    }

    public List<Song> getFavoriteSongs() {
        return favoriteSongs;
    }

    public List<User> getFollowing() {
        return following;
    }

    public void setFollowing(List<User> following) {
        this.following = following;
    }

    public List<User> getFollowers() {
        return followers;
    }

    public void setFollowers(List<User> followers) {
        this.followers = followers;
    }

    public Boolean getModerator() {
        return moderator;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

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

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
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
        private Image image;

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

        public Builder image(Image image) {
            this.image = image;
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
            user.image = this.image;
            return user;
        }
    }

    // Método para convertir a JSON
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"type\":\"").append("user").append("\",");
        json.append("\"id\":").append(id).append(",");
        json.append("\"name\":\"").append(username).append("\",");
        json.append("\"email\":\"").append(email).append("\",");
        json.append("\"bio\":\"").append(bio).append("\",");
        json.append("\"createdAt\":\"").append(createdAt != null ? createdAt.toString() : null).append("\",");
        json.append("\"updatedAt\":\"").append(updatedAt != null ? updatedAt.toString() : null).append("\",");
        json.append("\"verified\":\"").append(verified).append("\",");
        json.append("\"moderator\":\"").append(moderator).append("\",");
        json.append("\"followersAmount\":\"").append(followersAmount).append("\",");
        json.append("\"followingAmount\":\"").append(followingAmount).append("\",");
        json.append("\"reviewAmount\":\"").append(reviewAmount).append("\",");
        json.append("\"imgId\":\"").append(getImage().getId()).append("\"");
        json.append("}");
        return json.toString();
    }

}
