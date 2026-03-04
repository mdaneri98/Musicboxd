package ar.edu.itba.paw.domain.review;

import java.util.Objects;

public class ReviewId {
    private final Long value;

    public ReviewId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Review ID must be positive");
        }
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReviewId)) return false;
        ReviewId reviewId = (ReviewId) o;
        return value.equals(reviewId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ReviewId{" + value + '}';
    }
}
