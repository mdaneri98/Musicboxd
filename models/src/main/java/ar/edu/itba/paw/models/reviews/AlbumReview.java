package ar.edu.itba.paw.models.reviews;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "album_review")
public class AlbumReview extends Review {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    public AlbumReview() {
        // Constructor vacío necesario para JPA
    }

    public AlbumReview(User user, Album album, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes, Boolean isBlocked) {
        super(user, title, description, rating, createdAt, likes, isBlocked);
        this.album = album;
    }

    public AlbumReview(Long id, User user, Album album, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes, Boolean isBlocked) {
        super(id, user, title, description, rating, createdAt, likes, isBlocked);
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
        return album.getImage();
    }

    @Override
    public String getItemType() {
        return "Album";
    }

    @Override
    public String getItemLink() {
        return "album/" + album.getId();
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }
}
