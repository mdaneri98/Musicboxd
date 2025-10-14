package ar.edu.itba.paw.services;


import ar.edu.itba.paw.models.dtos.ArtistDTO;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
import java.util.List;


public interface ArtistService extends CrudService<ArtistDTO> {


    List<ArtistDTO> findBySongId(Long id);

    List<ArtistDTO> findByNameContaining(String sub, Integer page, Integer size);

    ArtistDTO create(ArtistDTO artistDTO);
    ArtistDTO update(ArtistDTO artistDTO);
    Boolean delete(ArtistDTO artistDTO);
    List<ReviewDTO> findReviewsByArtistId(Long artistId);

    Boolean updateRating(Long artistId, Double roundedAvgRating, Integer ratingAmount);
    Boolean hasUserReviewed(Long userId, Long artistId);
    
    // Count methods for pagination
    Long countAll();

}
