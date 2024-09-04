package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.reviews.AlbumReview;
import java.util.List;
import java.util.Optional;

public interface AlbumReviewDao {
    Optional<AlbumReview> findById(long id);
    List<AlbumReview> findAll();
    List<AlbumReview> findByAlbumId(long id);
    int save(AlbumReview albumReview);
    int deleteById(long id);
}
