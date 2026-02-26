package ar.edu.itba.paw.domain.services;

import ar.edu.itba.paw.domain.review.Rating;
import ar.edu.itba.paw.models.reviews.Review;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Domain service for rating calculations.
 * Centralizes rating logic that was previously duplicated across multiple services.
 *
 * Part of the Hexagonal Architecture migration (Phase 3).
 */
@Component
public class RatingService {

    /**
     * Calculates the average rating from a list of reviews.
     * Filters out blocked reviews before calculating.
     *
     * @param reviews List of reviews to calculate rating from
     * @return RatingResult containing the average rating and count of reviews
     */
    public RatingResult calculate(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return RatingResult.empty();
        }

        // Filter out blocked reviews and convert to Rating value objects
        List<Rating> ratings = reviews.stream()
                .filter(r -> !r.isBlocked())
                .map(Review::getRatingValue)
                .collect(Collectors.toList());

        if (ratings.isEmpty()) {
            return RatingResult.empty();
        }

        double average = Rating.calculateAverage(ratings);

        return new RatingResult(average, ratings.size());
    }

    /**
     * Result object containing rating calculation results.
     *
     * @param average The average rating value (0.0 if no reviews)
     * @param count The number of reviews included in the calculation
     */
    public record RatingResult(double average, int count) {
        /**
         * Creates an empty result (0.0 average, 0 count).
         *
         * @return Empty RatingResult
         */
        public static RatingResult empty() {
            return new RatingResult(0.0, 0);
        }
    }
}
