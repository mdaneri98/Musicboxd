package ar.edu.itba.paw.models.reviews;

import java.time.LocalDateTime;

public class SongReview extends Review {

    private Long songId;

    public SongReview(Long userId, Long songId, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes) {
        super(userId, title, description, rating, createdAt, likes);
        this.songId = songId;
    }

    public SongReview(Long id, Long songId, Long userId, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes) {
        super(id, userId, title, description, rating, createdAt, likes);
        this.songId = songId;
    }

    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
    }
}
