package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import java.util.List;

public interface AlbumDao extends CrudDao<Album> {

    List<Album> findByArtistId(Long id);
    List<Album> findByTitleContaining(String sub);

    Boolean updateRating(Long albumId, Double newRating, Integer newRatingAmount);
    Boolean hasUserReviewed(Long userId, Long albumId);

    Boolean deleteReviewsFromAlbum(Long albumId);
    List<AlbumReview> findReviewsByAlbumId(Long albumId);
}
