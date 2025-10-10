package ar.edu.itba.paw.services;


import ar.edu.itba.paw.models.dtos.ArtistDTO;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import java.util.List;

public interface ArtistService extends CrudService<ArtistDTO> {


    List<ArtistDTO> findBySongId(long id);

    List<ArtistDTO> findByNameContaining(String sub);

    ArtistDTO create(ArtistDTO artistDTO);
    ArtistDTO update(ArtistDTO artistDTO);
    boolean delete(ArtistDTO artistDTO);
    List<ArtistReview> findReviewsByArtistId(long artistId);

    boolean updateRating(Long artistId, Double roundedAvgRating, Integer ratingAmount);
    boolean hasUserReviewed(long userId, long artistId);

}
