package ar.edu.itba.paw.domain.artist;

import ar.edu.itba.paw.domain.events.DomainEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Artist {
    private final ArtistId id;
    private String name;
    private String bio;
    private Long imageId;
    private int ratingCount;
    private double avgRating;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private Artist(ArtistId id, String name, String bio, Long imageId,
                   int ratingCount, double avgRating,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.imageId = imageId;
        this.ratingCount = ratingCount;
        this.avgRating = avgRating;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Artist create(String name, String bio, Long imageId) {
        validateName(name);
        validateImageId(imageId);

        LocalDateTime now = LocalDateTime.now();
        return new Artist(null, name, bio, imageId, 0, 0.0, now, now);
    }

    public static Artist reconstitute(ArtistId id, String name, String bio, Long imageId,
                                     int ratingCount, double avgRating,
                                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (id == null) {
            throw new IllegalArgumentException("ID required for reconstitution");
        }
        return new Artist(id, name, bio, imageId, ratingCount, avgRating, createdAt, updatedAt);
    }

    public void updateInfo(String name, String bio) {
        validateName(name);
        this.name = name;
        this.bio = bio;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateImage(Long imageId) {
        validateImageId(imageId);
        this.imageId = imageId;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateRating(double newAvgRating, int newRatingCount) {
        if (newRatingCount < 0) {
            throw new IllegalArgumentException("Rating count cannot be negative");
        }
        if (newAvgRating < 0.0 || newAvgRating > 5.0) {
            throw new IllegalArgumentException("Average rating must be between 0 and 5");
        }
        this.avgRating = newAvgRating;
        this.ratingCount = newRatingCount;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementRatingCount() {
        this.ratingCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public void decrementRatingCount() {
        if (this.ratingCount > 0) {
            this.ratingCount--;
            this.updatedAt = LocalDateTime.now();
        }
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Artist name cannot be blank");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Artist name must not exceed 100 characters");
        }
    }

    private static void validateImageId(Long imageId) {
        if (imageId == null || imageId <= 0) {
            throw new IllegalArgumentException("Image ID must be positive");
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

    public ArtistId getId() {
        return id;
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

    public int getRatingCount() {
        return ratingCount;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Artist)) return false;
        Artist artist = (Artist) o;
        return id != null && id.equals(artist.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
