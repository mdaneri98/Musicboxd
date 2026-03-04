package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.ReviewId;
import ar.edu.itba.paw.domain.review.ReviewRepository;
import ar.edu.itba.paw.domain.user.UserId;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UnlikeReviewUseCase implements UnlikeReview {

    private final ReviewRepository reviewRepository;

    @Autowired
    public UnlikeReviewUseCase(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void execute(Long reviewId, Long userId) {
        reviewRepository.removeLike(new ReviewId(reviewId), new UserId(userId));
    }
}
