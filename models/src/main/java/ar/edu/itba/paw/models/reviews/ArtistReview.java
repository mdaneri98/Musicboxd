package ar.edu.itba.paw.models.reviews;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.User;

import java.time.LocalDateTime;

public class ArtistReview extends Review {
    private Artist artist;

    public ArtistReview(User user, Artist artist,String title, String description, Integer rating, LocalDateTime createdAt, Integer likes) {
        super(user, title, description, rating, createdAt, likes);
        this.artist = artist;
    }

    public ArtistReview(Long id, Artist artist, User user, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes) {
        super(id, user, title, description, rating, createdAt, likes);
        this.artist = artist;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }
}
