package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.dtos.AlbumDTO;
import java.util.List;
import ar.edu.itba.paw.models.dtos.ReviewDTO;

public interface AlbumService extends CrudService<AlbumDTO> {

    List<AlbumDTO> findByArtistId(Long id);

    List<AlbumDTO> findByTitleContaining(String sub);

    Boolean createAll(List<AlbumDTO> albumsDTO, Long artistId);

    Boolean updateAll(List<AlbumDTO> albumsDTO, Long artistId);

    Boolean updateRating(Long albumId, Double newRating, Integer newRatingAmount);

    Boolean hasUserReviewed(Long userId, Long albumId);

    List<ReviewDTO> findReviewsByAlbumId(Long albumId);
}
