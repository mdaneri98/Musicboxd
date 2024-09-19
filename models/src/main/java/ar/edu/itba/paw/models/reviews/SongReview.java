package ar.edu.itba.paw.models.reviews;

import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;

import java.time.LocalDateTime;

public class SongReview extends Review {

    private Song song;

    public SongReview(User user, Song song, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes) {
        super(user, title, description, rating, createdAt, likes);
        this.song = song;
    }

    public SongReview(Long id, User user,Song song, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes) {
        super(id, user, title, description, rating, createdAt, likes);
        this.song = song;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }
}
