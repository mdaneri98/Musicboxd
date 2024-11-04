package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.reviews.ArtistReview;

import java.util.List;

public interface ArtistDao extends CrudDao<Artist> {

    List<Artist> findBySongId(long id);
    List<Artist> findByNameContaining(String sub);

    boolean updateRating(long artistId, Double newRating, int newRatingAmount);
    boolean hasUserReviewed(long userId, long artistId);

    boolean deleteReviewsFromArtist(long artistId);

    List<ArtistReview> findReviewsByArtistId(long artistId);
}

