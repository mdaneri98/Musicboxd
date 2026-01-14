package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.reviews.Review;

import java.util.List;

public interface ArtistService extends CrudService<Artist> {

    List<Artist> findBySongId(Long id);

    List<Artist> findByNameContaining(String sub, Integer page, Integer size);

    List<Review> findReviewsByArtistId(Long artistId);

    Boolean updateRating(Long artistId);

    Boolean hasUserReviewed(Long userId, Long artistId);

    Long countAll();


}
