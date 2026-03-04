package ar.edu.itba.paw.domain.review;

import ar.edu.itba.paw.domain.events.DomainEvent;
import ar.edu.itba.paw.domain.rating.Rating;
import ar.edu.itba.paw.domain.user.UserId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Review {
    private final ReviewId id;
    private final UserId userId;
    private String title;
    private String description;
    private Rating rating;
    private final LocalDateTime createdAt;
    private int likes;
    private boolean blocked;
    private int commentAmount;
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected Review(ReviewId id, UserId userId, String title, String description,
                     Rating rating, LocalDateTime createdAt, int likes, boolean blocked,
                     int commentAmount) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.createdAt = createdAt;
        this.likes = likes;
        this.blocked = blocked;
        this.commentAmount = commentAmount;
    }

    protected static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Review title cannot be blank");
        }
        if (title.length() > 50) {
            throw new IllegalArgumentException("Review title must not exceed 50 characters");
        }
    }

    protected static void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Review description cannot be blank");
        }
        if (description.length() > 2000) {
            throw new IllegalArgumentException("Review description must not exceed 2000 characters");
        }
    }

    public void updateContent(String title, String description, Rating rating) {
        validateTitle(title);
        validateDescription(description);
        this.title = title;
        this.description = description;
        this.rating = rating;
    }

    public void block() {
        if (this.blocked) {
            throw new IllegalStateException("Review is already blocked");
        }
        this.blocked = true;
    }

    public void unblock() {
        if (!this.blocked) {
            throw new IllegalStateException("Review is not blocked");
        }
        this.blocked = false;
    }

    public void incrementLikes() {
        this.likes++;
    }

    public void decrementLikes() {
        if (this.likes > 0) {
            this.likes--;
        }
    }

    public void incrementCommentCount() {
        this.commentAmount++;
    }

    public void decrementCommentCount() {
        if (this.commentAmount > 0) {
            this.commentAmount--;
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

    public abstract ReviewType getType();
    public abstract Long getItemId();

    public ReviewId getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Rating getRating() {
        return rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getLikes() {
        return likes;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public int getCommentAmount() {
        return commentAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review)) return false;
        Review review = (Review) o;
        return id != null && id.equals(review.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
