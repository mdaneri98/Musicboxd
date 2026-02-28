package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.reviews.ArtistReview;

import java.util.List;

public interface ArtistDao extends CrudDao<Artist> {

    List<Artist> findBySongId(Long id);
    List<Artist> findByNameContaining(String sub, Integer page, Integer size);

    Boolean updateRating(Long artistId, Double newRating, Integer newRatingAmount);
    Boolean hasUserReviewed(Long userId, Long artistId);

    Boolean deleteReviewsFromArtist(Long artistId);

    List<ArtistReview> findReviewsByArtistId(Long artistId);

    Long countAll();
}
