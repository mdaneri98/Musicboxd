package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.Review;
import ar.edu.itba.paw.domain.review.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetAllReviewsUseCase implements GetAllReviews {

    private final ReviewRepository reviewRepository;

    @Autowired
    public GetAllReviewsUseCase(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<Review> execute(Integer page, Integer size) {
        return reviewRepository.findAll(page, size);
    }
}
