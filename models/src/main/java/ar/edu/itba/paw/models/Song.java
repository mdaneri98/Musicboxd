package ar.edu.itba.paw.models;

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
    private Album album;

    public Song(Long id, String title, String duration, Integer trackNumber, LocalDate createdAt, LocalDate updatedAt, Album album) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.trackNumber = trackNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.album = album;
    }

    public Song(Long id, String title, String duration, Album album) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.album = album;
    }

    public Song(String title, String duration, Integer trackNumber, Album album) {
        this.title = title;
        this.duration = duration;
        this.trackNumber = trackNumber;
        this.album = album;
    }

    public Song(Long id) {
        this.id = id;
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

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album albumId) {
        this.album = album;
    }

}
