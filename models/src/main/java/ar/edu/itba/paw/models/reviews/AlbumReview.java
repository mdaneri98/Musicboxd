package ar.edu.itba.paw.models.reviews;

import ar.edu.itba.paw.infrastructure.jpa.AlbumJpaEntity;
import ar.edu.itba.paw.models.Image;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "album_review")
@PrimaryKeyJoinColumn(name = "review_id")
public class AlbumReview extends Review {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private AlbumJpaEntity album;

    public AlbumReview() {
        // Constructor vacío necesario para JPA
    }

    public AlbumReview(Long userId, AlbumJpaEntity album, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes, Boolean isBlocked, Integer commentAmount) {
        super(userId, title, description, rating, createdAt, likes, isBlocked, commentAmount);
        this.album = album;
    }

    public AlbumReview(Long id, Long userId, AlbumJpaEntity album, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes, Boolean isBlocked, Integer commentAmount) {
        super(id, userId, title, description, rating, createdAt, likes, isBlocked, commentAmount);
        this.album = album;
    }

    @Override
    public String getItemName() {
        return album.getTitle();
    }

    @Override
    public Long getItemId() {
        return album.getId();
    }

    @Override
    public Image getItemImage() {
        if (album.getImageId() != null) {
            return new Image(album.getImageId(), null);
        }
        return null;
    }

    @Override
    public ReviewType getItemType() {
        return ReviewType.ALBUM;
    }

    public AlbumJpaEntity getAlbum() {
        return album;
    }

    public void setAlbum(AlbumJpaEntity album) {
        this.album = album;
    }
}

