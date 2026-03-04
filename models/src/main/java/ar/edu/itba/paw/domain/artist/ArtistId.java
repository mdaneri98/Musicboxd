package ar.edu.itba.paw.domain.artist;

import java.util.Objects;

public class ArtistId {
    private final Long value;

    public ArtistId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Artist ID must be positive");
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
        return value.equals(artistId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "ArtistId{" + value + '}';
    }
}
