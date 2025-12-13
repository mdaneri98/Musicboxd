package ar.edu.itba.paw.api.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ArtistDTO {

    private Long id;
    private String name;
    private String bio;
    private Integer ratingCount;
    private Double avgRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AlbumDTO> albums;
    private Long imageId;
    private Boolean isReviewed;
    private Boolean isFavorite;

    public ArtistDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
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

    public List<AlbumDTO> getAlbums() {
        return albums;
    }

    public void setAlbums(List<AlbumDTO> albums) {
        this.albums = albums;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public Boolean isReviewed() {
        if (isReviewed == null) {
            return false;
        }
        return isReviewed;
    }

    public void setIsReviewed(Boolean isReviewed) {
        this.isReviewed = isReviewed;
    }

    public Boolean isFavorite() {
        if (isFavorite == null) {
            return false;
        }
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, bio, ratingCount, avgRating, createdAt, updatedAt, albums, imageId, isReviewed, isFavorite);
    }
}

