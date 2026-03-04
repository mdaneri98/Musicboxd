package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.Review;
import ar.edu.itba.paw.domain.review.ReviewId;
import ar.edu.itba.paw.domain.review.ReviewRepository;
import ar.edu.itba.paw.exception.not_found.ReviewNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetReviewByIdUseCase implements GetReviewById {

    private final ReviewRepository reviewRepository;

    @Autowired
    public GetReviewByIdUseCase(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Review execute(Long reviewId) {
        return reviewRepository.findById(new ReviewId(reviewId))
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
    }
}
