package ar.edu.itba.paw;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Song {
    private Long id;
    private String title;
    private String duration;
    private Integer trackNumber;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long albumId;

    public Song(Long id, String title, String duration, Integer trackNumber, LocalDate createdAt, LocalDate updatedAt, Long albumId) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.trackNumber = trackNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.albumId = albumId;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(Integer trackNumber) {
        this.trackNumber = trackNumber;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }
}
