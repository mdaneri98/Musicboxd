package ar.edu.itba.paw.reviews;

import java.time.LocalDateTime;

public class AlbumReview extends Review {
    private Long albumId;

    public AlbumReview(Long userId, Long albumId, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes) {
        super(userId, title, description, rating, createdAt, likes);
        this.albumId = albumId;
    }

    public AlbumReview(Long id, Long albumId, Long userId, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes) {
        super(id, userId, title, description, rating, createdAt, likes);
        this.albumId = albumId;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }
}

