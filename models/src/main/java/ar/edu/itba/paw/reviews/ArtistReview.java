package ar.edu.itba.paw.reviews;

import java.time.LocalDateTime;

public class ArtistReview extends Review {
    private Long artistId;

    public ArtistReview(Long userId, Long artistId,String title, String description, Integer rating, LocalDateTime createdAt, Integer likes) {
        super(userId, title, description, rating, createdAt, likes);
        this.artistId = artistId;
    }

    public ArtistReview(Long id, Long artistId, Long userId, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes) {
        super(id, userId, title, description, rating, createdAt, likes);
        this.artistId = artistId;
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }
}
