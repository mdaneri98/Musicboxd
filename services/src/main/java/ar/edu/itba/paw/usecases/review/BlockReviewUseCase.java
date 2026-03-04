package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.Review;
import ar.edu.itba.paw.domain.review.ReviewId;
import ar.edu.itba.paw.domain.review.ReviewRepository;
import ar.edu.itba.paw.exception.not_found.ReviewNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BlockReviewUseCase implements BlockReview {

    private final ReviewRepository reviewRepository;

    @Autowired
    public BlockReviewUseCase(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void execute(Long reviewId) {
        Review review = reviewRepository.findById(new ReviewId(reviewId))
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
        review.block();
        reviewRepository.save(review);
    }
}
