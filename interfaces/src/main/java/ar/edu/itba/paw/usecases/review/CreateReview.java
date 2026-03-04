package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.Review;

public interface CreateReview {
    Review execute(CreateReviewCommand command);
}
