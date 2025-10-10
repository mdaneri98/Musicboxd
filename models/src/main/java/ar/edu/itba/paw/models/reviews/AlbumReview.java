package ar.edu.itba.paw.models.reviews;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.dtos.ReviewDTO;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "album_review")
@PrimaryKeyJoinColumn(name = "review_id")
public class AlbumReview extends Review {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    public AlbumReview() {
        // Constructor vacío necesario para JPA
    }

    public AlbumReview(User user, Album album, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes, Boolean isBlocked, Integer commentAmount) {
        super(user, title, description, rating, createdAt, likes, isBlocked, commentAmount);
        this.album = album;
    }

    public AlbumReview(Long id, User user, Album album, String title, String description, Integer rating, LocalDateTime createdAt, Integer likes, Boolean isBlocked, Integer commentAmount) {
        super(id, user, title, description, rating, createdAt, likes, isBlocked, commentAmount);
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

    public ReviewDTO toDTO() {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setId(getId());
        reviewDTO.setUserId(getUser().getId());
        reviewDTO.setUsername(getUser().getUsername());
        reviewDTO.setTitle(getTitle());
        reviewDTO.setDescription(getDescription());
        reviewDTO.setRating(getRating());
        reviewDTO.setCreatedAt(getCreatedAt());
        reviewDTO.setLikes(getLikes());
        reviewDTO.setIsBlocked(isBlocked());
        reviewDTO.setCommentAmount(getCommentAmount());
        reviewDTO.setItemType("Album");
        reviewDTO.setItemId(album.getId());
        reviewDTO.setItemName(album.getTitle());
        reviewDTO.setItemImageId(album.getImage().getId());
        return reviewDTO;
    }
}

