package ar.edu.itba.paw.domain.album;

import java.util.Objects;

public class AlbumId {
    private final Long value;

    public AlbumId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Album ID must be positive");
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
        return value.equals(albumId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "AlbumId{" + value + '}';
    }
}
