package ar.edu.itba.paw.models.reviews;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "artist_review")
public class ArtistReview extends Review {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    public ArtistReview() {
        // Constructor vacío necesario para JPA
    }

    public ArtistReview(User user, Artist artist, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes, Boolean isBlocked) {
        super(user, title, description, rating, createdAt, likes, isBlocked);
        this.artist = artist;
    }

    public ArtistReview(Long id, User user, Artist artist, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes, Boolean isBlocked) {
        super(id, user, title, description, rating, createdAt, likes, isBlocked);
        this.artist = artist;
    }

    @Override
    public String getItemName() {
        return artist.getName();
    }

    @Override
    public Long getItemId() {
        return artist.getId();
    }

    @Override
    public Long getItemImgId() {
        return artist.getImgId();
    }

    @Override
    public String getItemType() {
        return "Artist";
    }

    @Override
    public String getItemLink() {
        return "artist/" + artist.getId();
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }
}
