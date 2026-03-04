package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.Review;
import java.util.List;

public interface GetReviewsByUserId {
    List<Review> execute(Long userId, Integer page, Integer size);
}
