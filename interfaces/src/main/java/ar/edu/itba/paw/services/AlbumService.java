package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.dtos.AlbumDTO;
import java.util.List;
import ar.edu.itba.paw.models.reviews.AlbumReview;

public interface AlbumService extends CrudService<Album> {

    boolean delete(Album album);

    List<Album> findByArtistId(long id);

    List<Album> findByTitleContaining(String sub);

    Album create(AlbumDTO albumDTO, long artistId);
    boolean createAll(List<AlbumDTO> albumsDTO, long artistId);

    Album update(AlbumDTO albumDTO);
    boolean updateAll(List<AlbumDTO> albumsDTO, long artistId);

    boolean updateRating(long albumId, Double newRating, int newRatingAmount);
    boolean hasUserReviewed(long userId, long albumId);
    List<AlbumReview> findReviewsByAlbumId(long albumId);
}
