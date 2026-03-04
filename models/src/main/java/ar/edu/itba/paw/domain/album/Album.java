package ar.edu.itba.paw.domain.album;

import ar.edu.itba.paw.domain.events.DomainEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Album {
    private final AlbumId id;
    private String title;
    private String genre;
    private LocalDate releaseDate;
    private Long imageId;
    private Long artistId;
    private int ratingCount;
    private double avgRating;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private Album(AlbumId id, String title, String genre, LocalDate releaseDate,
                  Long imageId, Long artistId, int ratingCount, double avgRating,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.imageId = imageId;
        this.artistId = artistId;
        this.ratingCount = ratingCount;
        this.avgRating = avgRating;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Album create(String title, String genre, LocalDate releaseDate,
                               Long imageId, Long artistId) {
        validateTitle(title);
        validateImageId(imageId);

        LocalDateTime now = LocalDateTime.now();
        return new Album(null, title, genre, releaseDate, imageId, artistId,
                        0, 0.0, now, now);
    }

    public static Album reconstitute(AlbumId id, String title, String genre,
                                    LocalDate releaseDate, Long imageId, Long artistId,
                                    int ratingCount, double avgRating,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (id == null) {
            throw new IllegalArgumentException("ID required for reconstitution");
        }
        return new Album(id, title, genre, releaseDate, imageId, artistId,
                        ratingCount, avgRating, createdAt, updatedAt);
    }

    public void updateInfo(String title, String genre, LocalDate releaseDate) {
        validateTitle(title);
        this.title = title;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateImage(Long imageId) {
        validateImageId(imageId);
        this.imageId = imageId;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateArtist(Long artistId) {
        this.artistId = artistId;
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

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Album title cannot be blank");
        }
        if (title.length() > 100) {
            throw new IllegalArgumentException("Album title must not exceed 100 characters");
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

    public AlbumId getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public Long getImageId() {
        return imageId;
    }

    public Long getArtistId() {
        return artistId;
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
        if (!(o instanceof Album)) return false;
        Album album = (Album) o;
        return id != null && id.equals(album.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
