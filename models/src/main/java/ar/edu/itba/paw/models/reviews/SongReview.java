package ar.edu.itba.paw.models.reviews;

import ar.edu.itba.paw.infrastructure.jpa.SongJpaEntity;
import ar.edu.itba.paw.models.Image;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "song_review")
@PrimaryKeyJoinColumn(name = "review_id")
public class SongReview extends Review {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private SongJpaEntity song;

    public SongReview() {
        // Constructor vacío necesario para JPA
    }

    public SongReview(Long userId, SongJpaEntity song, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes, Boolean isBlocked, Integer commentAmount) {
        super(userId, title, description, rating, createdAt, likes, isBlocked, commentAmount);
        this.song = song;
    }

    public SongReview(Long id, Long userId,SongJpaEntity song, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes, Boolean isBlocked, Integer commentAmount) {
        super(id, userId, title, description, rating, createdAt, likes, isBlocked, commentAmount);
        this.song = song;
    }

    @Override
    public String getItemName() {
        return song.getTitle();
    }

    @Override
    public Long getItemId() {
        return song.getId();
    }

    @Override
    public Image getItemImage() {
        return null;
    }

    @Override
    public ReviewType getItemType() {
        return ReviewType.SONG;
    }

    public SongJpaEntity getSong() {
        return song;
    }

    public void setSong(SongJpaEntity song) {
        this.song = song;
    }
}
