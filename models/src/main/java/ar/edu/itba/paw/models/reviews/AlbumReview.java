package ar.edu.itba.paw.models.reviews;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.User;

import java.time.LocalDateTime;

public class AlbumReview extends Review {
    private Album album;

    public AlbumReview(User user, Album album, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes) {
        super(user, title, description, rating, createdAt, likes);
        this.album = album;
    }

    public AlbumReview(Long id, User user, Album album, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes) {
        super(id, user, title, description, rating, createdAt, likes);
        this.album = album;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbumId(Album album) {
        this.album = album;
    }
}

