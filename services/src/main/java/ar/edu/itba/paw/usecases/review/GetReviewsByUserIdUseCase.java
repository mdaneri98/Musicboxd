package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.Review;
import ar.edu.itba.paw.domain.review.ReviewRepository;
import ar.edu.itba.paw.domain.user.UserId;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetReviewsByUserIdUseCase implements GetReviewsByUserId {

    private final ReviewRepository reviewRepository;

    @Autowired
    public GetReviewsByUserIdUseCase(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<Review> execute(Long userId, Integer page, Integer size) {
        return reviewRepository.findByUserId(new UserId(userId), page, size);
    }
}
