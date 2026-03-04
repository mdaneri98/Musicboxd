package ar.edu.itba.paw.domain.rating;

import java.util.Collection;

public class RatingCalculator {

    public static double calculateAverage(Collection<Rating> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            return 0.0;
        }

        double average = ratings.stream()
            .mapToInt(Rating::getValue)
            .average()
            .orElse(0.0);

        return roundToTwoDecimals(average);
    }

    public static double calculateAverageFromIntegers(Collection<Integer> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            return 0.0;
        }

        double average = ratings.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);

        return roundToTwoDecimals(average);
    }

    private static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
