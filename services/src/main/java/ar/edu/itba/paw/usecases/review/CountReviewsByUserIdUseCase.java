package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.ReviewRepository;
import ar.edu.itba.paw.domain.user.UserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CountReviewsByUserIdUseCase implements CountReviewsByUserId {

    private final ReviewRepository reviewRepository;

    @Autowired
    public CountReviewsByUserIdUseCase(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Long execute(Long userId) {
        return reviewRepository.countByUserId(new UserId(userId));
    }
}
