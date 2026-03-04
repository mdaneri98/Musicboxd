package ar.edu.itba.paw.webapp.mapper.dto;

import ar.edu.itba.paw.infrastructure.jpa.ArtistJpaEntity;
import ar.edu.itba.paw.infrastructure.jpa.AlbumJpaEntity;
import ar.edu.itba.paw.infrastructure.jpa.SongJpaEntity;
import ar.edu.itba.paw.webapp.form.ReviewForm;
import ar.edu.itba.paw.models.reviews.*;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert ReviewForm to Review model
 */
@Component
public class ReviewFormMapper {

    public Review toModel(Long id, ReviewForm form) {
        Review review = toModel(form);
        review.setId(id);
        return review;
    }

    public Review toModel(ReviewForm form) {
        if (form == null) {
            return null;
        }

        ReviewType type = ReviewType.valueOf(form.getItemType());
        Review review = switch (type) {
            case ARTIST -> new ArtistReview();
            case ALBUM -> new AlbumReview();
            case SONG -> new SongReview();
        };

        review.setTitle(form.getTitle());
        review.setDescription(form.getDescription());
        review.setRating(form.getRating());

        if (form.isBlocked() != null) {
            review.setBlocked(form.isBlocked());
        }
        return review;
    }

    public Review toModel(ReviewForm form, Long userId, Long itemId) {
        Review review = toModel(form);
        if (review != null) {
            review.setUserId(userId);

            ReviewType type = ReviewType.valueOf(form.getItemType());
            setItemReference(review, itemId, type);
        }
        return review;
    }

    private void setItemReference(Review review, Long itemId, ReviewType type) {
        switch (type) {
            case ARTIST -> {
                if (review instanceof ArtistReview ar) {
                    ArtistJpaEntity artist = new ArtistJpaEntity();
                    artist.setId(itemId);
                    ar.setArtist(artist);
                }
            }
            case ALBUM -> {
                if (review instanceof AlbumReview alr) {
                    AlbumJpaEntity album = new AlbumJpaEntity();
                    album.setId(itemId);
                    alr.setAlbum(album);
                }
            }
            case SONG -> {
                if (review instanceof SongReview sr) {
                    SongJpaEntity song = new SongJpaEntity();
                    song.setId(itemId);
                    sr.setSong(song);
                }
            }
        }
    }

}
