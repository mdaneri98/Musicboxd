package ar.edu.itba.paw.models;

import java.time.LocalDate;
import java.time.LocalDate;
import java.util.Objects;

public class Album {
    private Long id;
    private String title;
    private String genre;
    private LocalDate releaseDate;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long imgId;
    private Artist artist;
    private Integer ratingCount;
    private Float avgRating;

    public Album(Long id, String title, String genre, LocalDate releaseDate, LocalDate createdAt, LocalDate updatedAt, Long imgId, Artist artist, Integer ratingCount, Float avgRating) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.imgId = imgId;
        this.artist = artist;
        this.ratingCount = ratingCount;
        this.avgRating = avgRating;
    }

    public Album(Long id, String title, Long imgId, String genre, Artist artist, LocalDate releaseDate) {
        this.id = id;
        this.title = title;
        this.imgId = imgId;
        this.artist = artist;
        this.releaseDate = releaseDate;
        this.genre = genre;
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

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Float avgRating) {
        this.avgRating = avgRating;
    }

    // Método para convertir a JSON
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"type\":\"").append("album").append("\",");
        json.append("\"id\":").append(id).append(",");
        json.append("\"name\":\"").append(title).append("\",");
        json.append("\"genre\":\"").append(genre).append("\",");
        json.append("\"releaseDate\":\"").append(releaseDate != null ? releaseDate.toString() : null).append("\",");
        json.append("\"createdAt\":\"").append(createdAt != null ? createdAt.toString() : null).append("\",");
        json.append("\"updatedAt\":\"").append(updatedAt != null ? updatedAt.toString() : null).append("\",");
        json.append("\"imgId\":").append(imgId).append(",");

        // Convertir el artista a JSON si no es nulo
        json.append("\"artist\":").append(artist != null ? artist.toJson() : null);

        json.append("}");
        return json.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        if (!Objects.equals(title, album.title)) return false;
        if (!Objects.equals(genre, album.genre)) return false;
        if (!Objects.equals(releaseDate, album.releaseDate)) return false;
        return Objects.equals(imgId, album.imgId);
    }

}
