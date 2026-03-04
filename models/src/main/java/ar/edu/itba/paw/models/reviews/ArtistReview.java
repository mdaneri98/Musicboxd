package ar.edu.itba.paw.models.reviews;

import ar.edu.itba.paw.infrastructure.jpa.ArtistJpaEntity;
import ar.edu.itba.paw.models.Image;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "artist_review")
@PrimaryKeyJoinColumn(name = "review_id")
public class ArtistReview extends Review {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private ArtistJpaEntity artist;

    public ArtistReview() {
        // Constructor vacío necesario para JPA
    }

    public ArtistReview(Long userId, ArtistJpaEntity artist,String title, String description, Integer rating, LocalDateTime createdAt, Integer likes, Boolean blocked, Integer commentAmount) {
        super(userId, title, description, rating, createdAt, likes, blocked, commentAmount);
        this.artist = artist;
    }

    public ArtistReview(Long id, ArtistJpaEntity artist, Long userId, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes, Boolean blocked, Integer commentAmount) {
        super(id, userId, title, description, rating, createdAt, likes, blocked, commentAmount);
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
    public Image getItemImage() {
        if (artist.getImageId() != null) {
            return new Image(artist.getImageId(), null);
        }
        return null;
    }

    @Override
    public ReviewType getItemType() {
        return ReviewType.ARTIST;
    }

    public ArtistJpaEntity getArtist() {
        return artist;
    }

    public void setArtist(ArtistJpaEntity artist) {
        this.artist = artist;
    }
}
