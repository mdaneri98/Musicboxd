package ar.edu.itba.paw;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Album {
    private Long id;
    private String title;
    private String genre;
    private Long artistId;
    private LocalDate releaseAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Album(Long id, String title, String genre, Long artistId, LocalDate releaseDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.artistId = artistId;
        this.releaseAt = releaseDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public LocalDate getReleaseAt() {
        return releaseAt;
    }

    public void setReleaseDate(LocalDate releaseAt) {
        this.releaseAt = releaseAt;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
