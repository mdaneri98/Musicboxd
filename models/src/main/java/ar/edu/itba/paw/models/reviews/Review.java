package ar.edu.itba.paw.models.reviews;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "comment_amount")
    private Integer commentAmount;

    @Transient
    private String timeAgo;

    @OneToMany(mappedBy = "review", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> comments;

    @ManyToMany
    @JoinTable(
            name = "review_like",
            joinColumns = @JoinColumn(name = "review_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    List<User> likedBy;

    public Review() {
        // Constructor vacío necesario para JPA
    }

    public Review(User user, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes, Boolean isBlocked, Integer commentAmount) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.createdAt = createdAt;
        this.likes = likes;
        this.isBlocked = isBlocked;
        this.commentAmount = commentAmount;
    }

    public Review(Long id, User user, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes, Boolean isBlocked, Integer commentAmount) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.createdAt = createdAt;
        this.likes = likes;
        this.isBlocked = isBlocked;
        this.commentAmount = commentAmount;
    }

    public abstract String getItemName();
    public abstract Long getItemId();
    public abstract Image getItemImage();
    public abstract String getItemType();
    public abstract String getItemLink();

    public Integer getCommentAmount() {
        return commentAmount;
    }

    public void setCommentAmount(Integer commentsAmount) {
        this.commentAmount = commentsAmount;
    }

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

    public Boolean getBlocked() {
        return isBlocked;
    }

    /*
    -> No debe accederse, puede haber muchos usuarios.
    public List<User> getLikedBy() {
        return likedBy;
    }
     */

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

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }
}
