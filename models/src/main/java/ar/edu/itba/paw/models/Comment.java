package ar.edu.itba.paw.models;

import ar.edu.itba.paw.infrastructure.jpa.UserJpaEntity;
import ar.edu.itba.paw.models.reviews.Review;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_id_seq")
    @SequenceGenerator(sequenceName = "comment_id_seq", name = "comment_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(name = "content", length = 255)
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Comment() { /* JPA needs it */ }

    public Comment(Long id, UserJpaEntity user, Review review, String content, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.review = review;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Comment(UserJpaEntity user, Review review, String content, LocalDateTime createdAt) {
        this.user = user;
        this.review = review;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Comment(UserJpaEntity user, Review review, String content) {
        this.user = user;
        this.review = review;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }


    public Review getReview() {
        return review;
    }

    public void setReview(Review reviewId) {
        this.review = reviewId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserJpaEntity getUser() {
        return user;
    }

    public void setUser(UserJpaEntity user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
