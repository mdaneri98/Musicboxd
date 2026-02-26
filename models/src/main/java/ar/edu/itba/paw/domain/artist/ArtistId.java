package ar.edu.itba.paw.domain.artist;

import java.util.Objects;

/**
 * Value Object representing an Artist identifier.
 * Provides type-safety for artist IDs.
 *
 * Part of the Hexagonal Architecture migration (Phase 4).
 */
public class ArtistId {
    private final Long value;

    public ArtistId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Artist ID must be a positive number");
        }
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArtistId)) return false;
        ArtistId artistId = (ArtistId) o;
        return Objects.equals(value, artistId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ArtistId{" + value + "}";
    }
}
