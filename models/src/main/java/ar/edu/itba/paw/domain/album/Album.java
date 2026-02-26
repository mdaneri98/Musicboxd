package ar.edu.itba.paw.domain.album;

import ar.edu.itba.paw.domain.artist.ArtistId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Rich domain entity representing an Album.
 * Contains business logic and validation, separated from persistence concerns.
 *
 * Part of the Hexagonal Architecture migration (Phase 4).
 */
public class Album {
    private final AlbumId id;
    private String title;
    private final String genre;
    private final ArtistId artistId;
    private final Long imageId;
    private final LocalDate releaseDate;
    private double avgRating;
    private int ratingCount;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Private constructor - use factory methods
    private Album(AlbumId id, String title, String genre, ArtistId artistId, Long imageId,
                  LocalDate releaseDate, double avgRating, int ratingCount,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.artistId = artistId;
        this.imageId = imageId;
        this.releaseDate = releaseDate;
        this.avgRating = avgRating;
        this.ratingCount = ratingCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Factory method to create a new Album.
     * Includes business validation.
     */
    public static Album create(String title, String genre, ArtistId artistId, Long imageId,
                               LocalDate releaseDate) {
        validateTitle(title);
        validateGenre(genre);
        validateReleaseDate(releaseDate);

        if (artistId == null) {
            throw new IllegalArgumentException("Artist ID cannot be null");
        }
        if (imageId == null || imageId <= 0) {
            throw new IllegalArgumentException("Image ID must be valid");
        }

        LocalDateTime now = LocalDateTime.now();
        return new Album(
                null, // ID will be assigned by persistence layer
                title,
                genre,
                artistId,
                imageId,
                releaseDate,
                0.0,
                0,
                now,
                now
        );
    }

    /**
     * Factory method to reconstitute an Album from persistence.
     * Used by repository adapters.
     */
    public static Album reconstitute(AlbumId id, String title, String genre, ArtistId artistId,
                                     Long imageId, LocalDate releaseDate, double avgRating,
                                     int ratingCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (id == null) {
            throw new IllegalArgumentException("Album ID cannot be null for reconstitution");
        }

        return new Album(id, title, genre, artistId, imageId, releaseDate,
                avgRating, ratingCount, createdAt, updatedAt);
    }

    /**
     * Business behavior: Change the album title.
     */
    public void changeTitle(String newTitle) {
        validateTitle(newTitle);
        this.title = newTitle;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business behavior: Update the rating.
     */
    public void updateRating(double avgRating, int ratingCount) {
        if (avgRating < 0.0 || avgRating > 5.0) {
            throw new IllegalArgumentException("Average rating must be between 0 and 5");
        }
        if (ratingCount < 0) {
            throw new IllegalArgumentException("Rating count cannot be negative");
        }

        this.avgRating = avgRating;
        this.ratingCount = ratingCount;
        this.updatedAt = LocalDateTime.now();
    }

    // Validation methods
    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Album title cannot be blank");
        }
        if (title.length() > 100) {
            throw new IllegalArgumentException("Album title max length is 100 characters");
        }
    }

    private static void validateGenre(String genre) {
        if (genre != null && genre.length() > 50) {
            throw new IllegalArgumentException("Genre max length is 50 characters");
        }
    }

    private static void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate != null && releaseDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Release date cannot be in the future");
        }
    }

    // Getters (immutability where possible)
    public AlbumId getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public ArtistId getArtistId() {
        return artistId;
    }

    public Long getImageId() {
        return imageId;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public int getRatingCount() {
        return ratingCount;
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
        return Objects.equals(id, album.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", artistId=" + artistId +
                '}';
    }
}
