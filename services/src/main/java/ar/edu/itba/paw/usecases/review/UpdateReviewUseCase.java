package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.rating.Rating;
import ar.edu.itba.paw.domain.review.Review;
import ar.edu.itba.paw.domain.review.ReviewId;
import ar.edu.itba.paw.domain.review.ReviewRepository;
import ar.edu.itba.paw.exception.not_found.ReviewNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateReviewUseCase implements UpdateReview {

    private final ReviewRepository reviewRepository;

    @Autowired
    public UpdateReviewUseCase(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Review execute(UpdateReviewCommand command) {
        Review review = reviewRepository.findById(new ReviewId(command.reviewId()))
                .orElseThrow(() -> new ReviewNotFoundException(command.reviewId()));

        review.updateContent(command.title(), command.description(), Rating.of(command.rating()));

        return reviewRepository.save(review);
    }
}
