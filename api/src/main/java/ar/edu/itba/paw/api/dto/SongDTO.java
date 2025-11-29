package ar.edu.itba.paw.api.dto;

import java.time.LocalDateTime;

public class SongDTO {

    private Long id;
    private String title;
    private String duration;
    private Integer trackNumber;
    private Long albumId;
    private Long artistId;
    private String albumTitle;
    private Long albumImageId;
    private Integer ratingCount;
    private Double avgRating;
    private LocalDateTime createdAt;
    private String formattedReleaseDate;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
    private Boolean isReviewed;
    private Boolean isFavorite;

    public SongDTO() {}

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

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public Long getAlbumImageId() {
        return albumImageId;
    }

    public void setAlbumImageId(Long albumImageId) {
        this.albumImageId = albumImageId;
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

    public Boolean isDeleted() {
        if (isDeleted == null) {
            return false;
        }
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
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

    public String getFormattedReleaseDate() {
        return formattedReleaseDate;
    }

    public void setFormattedReleaseDate(String formattedReleaseDate) {
        this.formattedReleaseDate = formattedReleaseDate;
    }
}

