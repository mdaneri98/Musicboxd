package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.Artist;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ArtistJdbcDao implements ArtistDao {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Artist> ROW_MAPPER = (rs, rowNum) -> new Artist(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("genre"),
            rs.getString("bio"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getObject("updated_at", LocalDateTime.class)
    );

    public ArtistJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Optional<Artist> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM artists WHERE id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<Artist> findAll() {
        return jdbcTemplate.query("SELECT * FROM artists", ROW_MAPPER);
    }

    @Override
    public int save(Artist artist) {
        return jdbcTemplate.update(
                "INSERT INTO artists (name, genre, bio, created_at, updated_at) VALUES (?, ?, ?, ?, ?)",
                artist.getName(),
                artist.getGenre(),
                artist.getBio(),
                artist.getCreatedAt(),
                artist.getUpdatedAt()
        );
    }

    @Override
    public int update(Artist artist) {
        return jdbcTemplate.update(
                "UPDATE artists SET name = ?, genre = ?, bio = ?, created_at = ?, updated_at = ? WHERE id = ?",
                artist.getName(),
                artist.getGenre(),
                artist.getBio(),
                artist.getCreatedAt(),
                artist.getUpdatedAt(),
                artist.getId()
        );
    }

    @Override
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM artists WHERE id = ?", id);
    }
}

