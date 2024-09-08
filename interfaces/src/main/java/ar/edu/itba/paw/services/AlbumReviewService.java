package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.reviews.AlbumReview;
import java.util.List;
import java.util.Optional;

public interface AlbumReviewService {
    Optional<AlbumReview> findById(long id);
    List<AlbumReview> findAll();
    List<AlbumReview> findByAlbumId(long id);
    int save(AlbumReview albumReview);
    int deleteById(long id);
}
