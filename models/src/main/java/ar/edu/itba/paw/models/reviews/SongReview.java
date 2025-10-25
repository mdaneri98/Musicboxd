package ar.edu.itba.paw.models.reviews;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "song_review")
@PrimaryKeyJoinColumn(name = "review_id")
public class SongReview extends Review {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    public SongReview() {
        // Constructor vacío necesario para JPA
    }

    public SongReview(User user, Song song, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes, Boolean isBlocked, Integer commentAmount) {
        super(user, title, description, rating, createdAt, likes, isBlocked, commentAmount);
        this.song = song;
    }

    public SongReview(Long id, User user,Song song, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes, Boolean isBlocked, Integer commentAmount) {
        super(id, user, title, description, rating, createdAt, likes, isBlocked, commentAmount);
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
        return song.getAlbum().getImage();
    }

    @Override
    public String getItemType() {
        return "Song";
    }

    @Override
    public String getItemLink() {
        return "song/" + song.getId();
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }
}
