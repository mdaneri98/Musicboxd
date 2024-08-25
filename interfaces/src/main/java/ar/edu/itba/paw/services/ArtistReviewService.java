package ar.edu.itba.paw.services;

import ar.edu.itba.paw.ArtistReview;
import java.util.List;
import java.util.Optional;

public interface ArtistReviewService {
    Optional<ArtistReview> findById(long id);
    List<ArtistReview> findAll();
    int save(ArtistReview artistReview);
    int update(ArtistReview artistReview);
    int deleteById(long id);
}
