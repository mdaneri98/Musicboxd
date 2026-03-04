package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.ArtistReview;

import java.util.Optional;

public interface GetReviewByUserAndArtist {
    Optional<ArtistReview> execute(Long userId, Long artistId);
}
