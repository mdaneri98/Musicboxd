package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import java.util.List;
import ar.edu.itba.paw.models.reviews.ArtistReview;

public interface ArtistService extends CrudService<Artist> {

    boolean delete(Artist artist);

    List<Artist> findBySongId(long id);

    List<Artist> findByNameContaining(String sub);

    Artist create(ArtistDTO artistDTO);
    Artist update(ArtistDTO artistDTO);
    List<ArtistReview> findReviewsByArtistId(long artistId);

    boolean updateRating(Long artistId, Double roundedAvgRating, Integer ratingAmount);
    boolean hasUserReviewed(long userId, long artistId);

}
