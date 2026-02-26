package ar.edu.itba.paw.usecases;

import ar.edu.itba.paw.models.reviews.Review;

public interface BlockReview {

    Review execute(BlockReviewCommand command);

    record BlockReviewCommand(
        Long reviewId,
        Long moderatorId,
        boolean block
    ) {
        public BlockReviewCommand {
            if (reviewId == null || reviewId <= 0) {
                throw new IllegalArgumentException("Review ID must be positive");
            }
            if (moderatorId == null || moderatorId <= 0) {
                throw new IllegalArgumentException("Moderator ID must be positive");
            }
        }
    }
}
