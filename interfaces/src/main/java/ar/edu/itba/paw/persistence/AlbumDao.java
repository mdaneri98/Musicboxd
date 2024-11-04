package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import java.util.List;

public interface AlbumDao extends CrudDao<Album> {

    List<Album> findByArtistId(long id);
    List<Album> findByTitleContaining(String sub);

    boolean updateRating(long albumId, Double newRating, int newRatingAmount);
    boolean hasUserReviewed(long userId, long albumId);

    boolean deleteReviewsFromAlbum(long albumId);
    List<AlbumReview> findReviewsByAlbumId(long albumId);
}
