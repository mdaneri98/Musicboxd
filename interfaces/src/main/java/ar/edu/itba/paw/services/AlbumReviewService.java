package ar.edu.itba.paw.services;

import ar.edu.itba.paw.reviews.AlbumReview;
import java.util.List;
import java.util.Optional;

public interface AlbumReviewService {
    Optional<AlbumReview> findById(long id);
    List<AlbumReview> findAll();
    int save(AlbumReview albumReview);
    int deleteById(long id);
}
