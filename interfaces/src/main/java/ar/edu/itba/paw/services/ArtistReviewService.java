package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.reviews.ArtistReview;
import java.util.List;
import java.util.Optional;

public interface ArtistReviewService {
    Optional<ArtistReview> findById(long id);
    List<ArtistReview> findAll();
    List<ArtistReview> findByArtistId(long id);
    int save(ArtistReview artistReview);
    int deleteById(long id);
}
