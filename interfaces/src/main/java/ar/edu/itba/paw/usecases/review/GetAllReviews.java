package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.Review;
import java.util.List;

public interface GetAllReviews {
    List<Review> execute(Integer page, Integer size);
}
