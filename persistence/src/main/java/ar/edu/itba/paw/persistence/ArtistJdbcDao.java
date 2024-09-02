package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.Artist;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ArtistJdbcDao implements ArtistDao {

    private final JdbcTemplate jdbcTemplate;

    public ArtistJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Optional<Artist> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM artist WHERE id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                SimpleRowMappers.ARTIST_ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<Artist> findAll() {
        return jdbcTemplate.query("SELECT * FROM artist", SimpleRowMappers.ARTIST_ROW_MAPPER);
    }

    @Override
    public List<Artist> findBySongId(long id) {
        return jdbcTemplate.query("SELECT DISTINCT a.* FROM artist a JOIN song_artist sa ON a.id = sa.artist_id WHERE sa.song_id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                SimpleRowMappers.ARTIST_ROW_MAPPER);
    }

    @Override
    public int save(Artist artist) {
        return jdbcTemplate.update(
                "INSERT INTO artist (name, bio, created_at, updated_at, img_src) VALUES (?, ?, ?, ?, ?)",
                artist.getName(),
                artist.getBio(),
                artist.getCreatedAt(),
                artist.getUpdatedAt(),
                artist.getImgId()
        );
    }

    @Override
    public int update(Artist artist) {
        return jdbcTemplate.update(
                "UPDATE artist SET name = ?, bio = ?, created_at = ?, updated_at = ?, img_src = ? WHERE id = ?",
                artist.getName(),
                artist.getBio(),
                artist.getCreatedAt(),
                artist.getUpdatedAt(),
                artist.getImgId(),
                artist.getId()
        );
    }

    @Override
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM artist WHERE id = ?", id);
    }
}

