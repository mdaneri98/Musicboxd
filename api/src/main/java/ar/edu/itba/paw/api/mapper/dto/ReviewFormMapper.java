package ar.edu.itba.paw.api.mapper.dto;

import ar.edu.itba.paw.api.form.ReviewForm;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
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
            User user = new User();
            user.setId(userId);
            review.setUser(user);
            
            ReviewType type = ReviewType.valueOf(form.getItemType());
            setItemReference(review, itemId, type);
        }
        return review;
    }

    private void setItemReference(Review review, Long itemId, ReviewType type) {
        switch (type) {
            case ARTIST -> {
                if (review instanceof ArtistReview ar) {
                    ar.setArtist(new Artist(itemId));
                }
            }
            case ALBUM -> {
                if (review instanceof AlbumReview alr) {
                    alr.setAlbum(new Album(itemId));
                }
            }
            case SONG -> {
                if (review instanceof SongReview sr) {
                    sr.setSong(new Song(itemId));
                }
            }
        }
    }

}
