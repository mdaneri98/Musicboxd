package ar.edu.itba.paw.domain.song;

import java.util.Objects;

public class SongId {
    private final Long value;

    public SongId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Song ID must be positive");
        }
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SongId songId = (SongId) o;
        return Objects.equals(value, songId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "SongId{" + value + '}';
    }
}
