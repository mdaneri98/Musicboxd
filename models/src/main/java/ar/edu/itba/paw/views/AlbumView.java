package ar.edu.itba.paw.views;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AlbumView {
    private final Long id;
    private final String title;
    private final String genre;
    private final LocalDate releaseDate;
    private final Long imageId;
    private final Long artistId;
    private final String artistName;
    private final int ratingCount;
    private final double avgRating;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public AlbumView(Long id, String title, String genre, LocalDate releaseDate,
                     Long imageId, Long artistId, String artistName,
                     int ratingCount, double avgRating,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.imageId = imageId;
        this.artistId = artistId;
        this.artistName = artistName;
        this.ratingCount = ratingCount;
        this.avgRating = avgRating;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
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

    public String getArtistName() {
        return artistName;
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
}
