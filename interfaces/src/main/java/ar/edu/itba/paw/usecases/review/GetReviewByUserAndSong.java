package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.SongReview;

import java.util.Optional;

public interface GetReviewByUserAndSong {
    Optional<SongReview> execute(Long userId, Long songId);
}
