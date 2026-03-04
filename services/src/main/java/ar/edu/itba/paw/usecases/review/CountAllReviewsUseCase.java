package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CountAllReviewsUseCase implements CountAllReviews {

    private final ReviewRepository reviewRepository;

    @Autowired
    public CountAllReviewsUseCase(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Long execute() {
        return reviewRepository.countAll();
    }
}
