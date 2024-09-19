package ar.edu.itba.paw.models;

import java.time.LocalDate;
import java.time.LocalDate;

public class Album {
    private Long id;
    private String title;
    private String genre;
    private LocalDate releaseDate;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long imgId;
    private Artist artist;

    public Album(Long id, String title, String genre, LocalDate releaseDate, LocalDate createdAt, LocalDate updatedAt, Long imgId, Artist artist) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.imgId = imgId;
        this.artist = artist;
    }

    public Album(Long id, String title, Long imgId, Artist artist, LocalDate releaseDate) {
        this.id = id;
        this.title = title;
        this.imgId = imgId;
        this.artist = artist;
        this.releaseDate = releaseDate;
    }

    public Album(String title, String genre, Long imgId, Artist artist) {
        this.title = title;
        this.genre = genre;
        this.imgId = imgId;
        this.artist = artist;
    }

    public Album(Long id) {
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

    public Long getImgId() {
        return imgId;
    }

    public void setImgId(Long imgId) {
        this.imgId = imgId;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }
}
