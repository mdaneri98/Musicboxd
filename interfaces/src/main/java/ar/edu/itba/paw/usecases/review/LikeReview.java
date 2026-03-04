package ar.edu.itba.paw.usecases.review;

public interface LikeReview {
    void execute(Long reviewId, Long userId);
}
