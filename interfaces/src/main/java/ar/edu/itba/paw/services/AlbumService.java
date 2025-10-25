package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.dtos.AlbumDTO;
import java.util.List;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
import ar.edu.itba.paw.models.Artist;

public interface AlbumService extends CrudService<AlbumDTO> {

    List<AlbumDTO> findByArtistId(Long id);

    List<AlbumDTO> findByTitleContaining(String sub, Integer page, Integer size);

    Boolean createAll(List<AlbumDTO> albumsDTO, Artist artist);

    Boolean updateAll(List<AlbumDTO> albumsDTO, Artist artist);

    Boolean updateRating(Long albumId, Double newRating, Integer newRatingAmount);

    Boolean hasUserReviewed(Long userId, Long albumId);

    List<ReviewDTO> findReviewsByAlbumId(Long albumId);
    
    // Count methods for pagination
    Long countAll();
}
