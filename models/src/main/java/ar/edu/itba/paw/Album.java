package ar.edu.itba.paw;

import java.time.LocalDate;
import java.time.LocalDate;

public class Album {
    private Long id;
    private String title;
    private String genre;
    private LocalDate releaseDate;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String imgSrc;
    private Long artistId;

    public Album(Long id, String title, String genre, LocalDate releaseDate, LocalDate createdAt, LocalDate updatedAt, String imgSrc, Long artistId) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.imgSrc = imgSrc;
        this.artistId = artistId;
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
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

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }
}
