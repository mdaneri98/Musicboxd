package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.ReviewId;
import ar.edu.itba.paw.domain.review.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteReviewUseCase implements DeleteReview {

    private final ReviewRepository reviewRepository;

    @Autowired
    public DeleteReviewUseCase(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void execute(Long reviewId) {
        reviewRepository.delete(new ReviewId(reviewId));
    }
}
