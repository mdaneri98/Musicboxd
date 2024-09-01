package ar.edu.itba.paw.services;

import ar.edu.itba.paw.reviews.SongReview;
import java.util.List;
import java.util.Optional;

public interface SongReviewService {
    Optional<SongReview> findById(long id);
    List<SongReview> findAll();
    int save(SongReview songReview);
    int deleteById(long id);
}
