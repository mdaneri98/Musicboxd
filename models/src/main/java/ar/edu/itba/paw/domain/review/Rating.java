package ar.edu.itba.paw.domain.review;

import java.util.List;
import java.util.Objects;

/**
 * Value Object representing a review rating.
 * Encapsulates validation and behavior related to ratings.
 *
 * Part of the Hexagonal Architecture migration (Phase 2).
 */
public class Rating {
    private final int value;

    /**
     * Creates a new Rating with the given value.
     *
     * @param value The rating value (must be between 1 and 5 inclusive)
     * @throws IllegalArgumentException if value is not between 1 and 5
     */
    public Rating(int value) {
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.value = value;
    }

    /**
     * Gets the rating value.
     *
     * @return The rating value (1-5)
     */
    public int getValue() {
        return value;
    }

    /**
     * Calculates the average rating from a list of ratings.
     * The result is rounded to 2 decimal places.
     *
     * @param ratings List of ratings to average
     * @return The average rating rounded to 2 decimal places, or 0.0 if the list is empty
     */
    public static double calculateAverage(List<Rating> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            return 0.0;
        }

        double average = ratings.stream()
                .mapToInt(Rating::getValue)
                .average()
                .orElse(0.0);

        // Round to 2 decimal places
        return Math.round(average * 100.0) / 100.0;
    }

    /**
     * Creates an initial rating (typically used for new entities).
     *
     * @return A Rating with value 0 (note: this bypasses validation for initialization purposes)
     */
    public static Rating initial() {
        // For initial state, we return a special case
        // This could be replaced with an Optional<Rating> pattern if preferred
        return new Rating(1); // Default to minimum valid rating
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rating)) return false;
        Rating rating = (Rating) o;
        return value == rating.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
