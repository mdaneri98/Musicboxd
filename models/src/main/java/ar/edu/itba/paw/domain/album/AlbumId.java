package ar.edu.itba.paw.domain.album;

import java.util.Objects;

/**
 * Value Object representing an Album identifier.
 * Provides type-safety for album IDs.
 *
 * Part of the Hexagonal Architecture migration (Phase 4).
 */
public class AlbumId {
    private final Long value;

    public AlbumId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Album ID must be a positive number");
        }
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlbumId)) return false;
        AlbumId albumId = (AlbumId) o;
        return Objects.equals(value, albumId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "AlbumId{" + value + "}";
    }
}
