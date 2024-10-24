package ar.edu.itba.paw.models.reviews;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.User;

import java.time.LocalDateTime;

public class AlbumReview extends Review {
    private Album album;

    public AlbumReview(User user, Album album, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes, Boolean blocked, Integer commentAmount) {
        super(user, title, description, rating, createdAt, likes, blocked, commentAmount);
        this.album = album;
    }

    public AlbumReview(Long id, User user, Album album, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes, Boolean blocked, Integer commentAmount) {
        super(id, user, title, description, rating, createdAt, likes, blocked, commentAmount);
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
    public Long getItemImgId() {
        return album.getImgId();
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

    public void setAlbumId(Album album) {
        this.album = album;
    }
}

