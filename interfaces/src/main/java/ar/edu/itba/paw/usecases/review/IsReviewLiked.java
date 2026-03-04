package ar.edu.itba.paw.usecases.review;

public interface IsReviewLiked {
    boolean execute(Long reviewId, Long userId);
}
