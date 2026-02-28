package ar.edu.itba.paw.models.reviews;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.Notification;

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

    @Column(name = "user_id", nullable = false)
    private Long userId;

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

    @Column(name = "isblocked")
    private Boolean isBlocked;

    @Column(name = "comment_amount")
    private Integer commentAmount;

    @OneToMany(mappedBy = "review", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> comments;


    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications;

    // Métodos abstractos para el item relacionado
    public abstract String getItemName();
    public abstract Long getItemId();
    public abstract Image getItemImage();
    public abstract ReviewType getItemType();

    @Transient
    private Boolean isLiked;


    public Review() {
        // Constructor vacío necesario para JPA
    }

    public Review(Long userId, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes, Boolean isBlocked, Integer commentAmount) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.createdAt = createdAt;
        this.likes = likes;
        this.isBlocked = isBlocked;
        this.commentAmount = commentAmount;
    }

    public Review(Long id, Long userId, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes, Boolean isBlocked, Integer commentAmount) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.createdAt = createdAt;
        this.likes = likes;
        this.isBlocked = isBlocked;
        this.commentAmount = commentAmount;
    }

    public Integer getCommentAmount() {
        return commentAmount;
    }

    public void setCommentAmount(Integer commentsAmount) {
        this.commentAmount = commentsAmount;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }
}
