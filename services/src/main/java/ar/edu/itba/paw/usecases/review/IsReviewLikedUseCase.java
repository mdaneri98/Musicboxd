package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.ReviewId;
import ar.edu.itba.paw.domain.review.ReviewRepository;
import ar.edu.itba.paw.domain.user.UserId;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class IsReviewLikedUseCase implements IsReviewLiked {

    private final ReviewRepository reviewRepository;

    @Autowired
    public IsReviewLikedUseCase(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public boolean execute(Long reviewId, Long userId) {
        return reviewRepository.isLikedByUser(new ReviewId(reviewId), new UserId(userId));
    }
}
