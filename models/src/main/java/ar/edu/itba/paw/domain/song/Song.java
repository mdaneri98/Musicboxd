package ar.edu.itba.paw.domain.song;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Song {
    private final SongId id;
    private String title;
    private String duration;
    private Integer trackNumber;
    private Long albumId;
    private List<Long> artistIds;
    private int ratingCount;
    private double avgRating;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Song(SongId id, String title, String duration, Integer trackNumber,
                 Long albumId, List<Long> artistIds, int ratingCount, double avgRating,
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.trackNumber = trackNumber;
        this.albumId = albumId;
        this.artistIds = artistIds != null ? new ArrayList<>(artistIds) : new ArrayList<>();
        this.ratingCount = ratingCount;
        this.avgRating = avgRating;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Song create(String title, String duration, Integer trackNumber, Long albumId, List<Long> artistIds) {
        validateTitle(title);
        validateDuration(duration);
        validateAlbumId(albumId);

        LocalDateTime now = LocalDateTime.now();
        return new Song(null, title, duration, trackNumber, albumId, artistIds, 0, 0.0, now, now);
    }

    public static Song reconstitute(SongId id, String title, String duration, Integer trackNumber,
                                     Long albumId, List<Long> artistIds, int ratingCount, double avgRating,
                                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        validateTitle(title);
        validateDuration(duration);
        validateAlbumId(albumId);

        return new Song(id, title, duration, trackNumber, albumId, artistIds, ratingCount, avgRating, createdAt, updatedAt);
    }

    public void updateInfo(String title, String duration, Integer trackNumber, Long albumId) {
        validateTitle(title);
        validateDuration(duration);
        validateAlbumId(albumId);

        this.title = title;
        this.duration = duration;
        this.trackNumber = trackNumber;
        this.albumId = albumId;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateRating(int ratingCount, double avgRating) {
        this.ratingCount = ratingCount;
        this.avgRating = avgRating;
        this.updatedAt = LocalDateTime.now();
    }

    public void setArtists(List<Long> artistIds) {
        this.artistIds = artistIds != null ? new ArrayList<>(artistIds) : new ArrayList<>();
        this.updatedAt = LocalDateTime.now();
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Song title cannot be blank");
        }
        if (title.length() > 100) {
            throw new IllegalArgumentException("Song title cannot exceed 100 characters");
        }
    }

    private static void validateDuration(String duration) {
        if (duration == null || duration.isBlank()) {
            throw new IllegalArgumentException("Song duration cannot be blank");
        }
        if (duration.length() > 10) {
            throw new IllegalArgumentException("Song duration cannot exceed 10 characters");
        }
    }

    private static void validateAlbumId(Long albumId) {
        if (albumId == null || albumId <= 0) {
            throw new IllegalArgumentException("Album ID must be positive");
        }
    }

    public SongId getId() {
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

    public List<Long> getArtistIds() {
        return new ArrayList<>(artistIds);
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
