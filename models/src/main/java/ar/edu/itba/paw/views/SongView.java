package ar.edu.itba.paw.views;

import java.time.LocalDateTime;
import java.util.List;

public class SongView {
    private final Long id;
    private final String title;
    private final String duration;
    private final Integer trackNumber;
    private final Long albumId;
    private final String albumTitle;
    private final Long albumImageId;
    private final List<Long> artistIds;
    private final int ratingCount;
    private final double avgRating;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public SongView(Long id, String title, String duration, Integer trackNumber,
                    Long albumId, String albumTitle, Long albumImageId,
                    List<Long> artistIds, int ratingCount, double avgRating,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.trackNumber = trackNumber;
        this.albumId = albumId;
        this.albumTitle = albumTitle;
        this.albumImageId = albumImageId;
        this.artistIds = artistIds;
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

    public String getDuration() {
        return duration;
    }

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public Long getAlbumImageId() {
        return albumImageId;
    }

    public List<Long> getArtistIds() {
        return artistIds;
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
