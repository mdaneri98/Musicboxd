package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.AlbumReview;

import java.util.Optional;

public interface GetReviewByUserAndAlbum {
    Optional<AlbumReview> execute(Long userId, Long albumId);
}
