package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.webapp.dto.links.AlbumLinksDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class AlbumDTO {

    private Long id;
    private String title;
    private String genre;
    private LocalDate releaseDate;
    private String formattedReleaseDate;
    private Long imageId;
    private Long artistId;
    private String artistName;
    private Integer ratingCount;
    private Double avgRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SongDTO> songs;
    private Boolean isDeleted;

    @JsonProperty("_links")
    private AlbumLinksDTO links;

    public AlbumDTO() {
    }

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

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
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

    public List<SongDTO> getSongs() {
        return songs;
    }

    public void setSongs(List<SongDTO> songs) {
        this.songs = songs;
    }

    public Boolean isDeleted() {
        if (isDeleted == null) {
            return false;
        }
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getFormattedReleaseDate() {
        return formattedReleaseDate;
    }

    public void setFormattedReleaseDate(String formattedReleaseDate) {
        this.formattedReleaseDate = formattedReleaseDate;
    }

    public AlbumLinksDTO getLinks() {
        return links;
    }

    public void setLinks(AlbumLinksDTO links) {
        this.links = links;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, genre, releaseDate, formattedReleaseDate, imageId, artistId, artistName,
                ratingCount, avgRating, createdAt, updatedAt, songs, isDeleted);
    }
}
