package ar.edu.itba.paw.domain.rating;

import java.util.Objects;

public class Rating {
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 5;

    private final int value;

    private Rating(int value) {
        this.value = value;
    }

    public static Rating of(int value) {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException(
                String.format("Rating must be between %d and %d, got: %d", MIN_VALUE, MAX_VALUE, value)
            );
        }
        return new Rating(value);
    }

    public int getValue() {
        return value;
    }

    public boolean isMinimum() {
        return value == MIN_VALUE;
    }

    public boolean isMaximum() {
        return value == MAX_VALUE;
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
