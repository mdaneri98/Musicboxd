package ar.edu.itba.paw.models.reviews;

import ar.edu.itba.paw.models.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_id_seq")
    @SequenceGenerator(sequenceName = "review_id_seq", name = "review_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false)
    private Integer rating;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Integer likes;

    @Transient
    private Boolean isLiked;

    @Column(name = "isblocked")
    private Boolean isBlocked;

    public Review() {
        // Constructor vacío necesario para JPA
    }

    public Review(User user, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes, Boolean isBlocked) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.createdAt = createdAt;
        this.likes = likes;
        this.isBlocked = isBlocked;
    }

    public Review(Long id, User user, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes, Boolean isBlocked) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.createdAt = createdAt;
        this.likes = likes;
        this.isBlocked = isBlocked;
    }

    public abstract String getItemName();
    public abstract Long getItemId();
    public abstract Long getItemImgId();
    public abstract String getItemType();
    public abstract String getItemLink();

    public Boolean getLiked() {
        return isLiked;
    }
    public Boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
