package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.AlbumReview;
import java.util.List;
import java.util.Optional;

public interface AlbumReviewDao {
    Optional<AlbumReview> findById(long id);
    List<AlbumReview> findAll();
    int save(AlbumReview albumReview);
    int update(AlbumReview albumReview);
    int deleteById(long id);
}
