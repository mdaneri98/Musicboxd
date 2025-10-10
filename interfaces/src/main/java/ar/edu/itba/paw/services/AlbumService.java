package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.dtos.AlbumDTO;
import java.util.List;
import ar.edu.itba.paw.models.reviews.AlbumReview;

public interface AlbumService extends CrudService<AlbumDTO> {

    List<AlbumDTO> findByArtistId(long id);

    List<AlbumDTO> findByTitleContaining(String sub);

    boolean createAll(List<AlbumDTO> albumsDTO, long artistId);

    boolean updateAll(List<AlbumDTO> albumsDTO, long artistId);

    boolean updateRating(long albumId, Double newRating, int newRatingAmount);

    boolean hasUserReviewed(long userId, long albumId);

    List<AlbumReview> findReviewsByAlbumId(long albumId);
}
