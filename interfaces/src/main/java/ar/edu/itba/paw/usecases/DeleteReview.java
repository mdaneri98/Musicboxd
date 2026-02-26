package ar.edu.itba.paw.usecases;

public interface DeleteReview {

    void execute(DeleteReviewCommand command);

    record DeleteReviewCommand(
        Long reviewId,
        Long userId
    ) {
        public DeleteReviewCommand {
            if (reviewId == null || reviewId <= 0) {
                throw new IllegalArgumentException("Review ID must be positive");
            }
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("User ID must be positive");
            }
        }
    }
}
