package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.Song;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class SongJdbcDao implements SongDao {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Song> ROW_MAPPER = (rs, rowNum) -> new Song(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getObject("duration", Duration.class),
            rs.getInt("track_number"),
            rs.getLong("album_id"),
            rs.getLong("artist_id"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getObject("updated_at", LocalDateTime.class)
    );

    public SongJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Optional<Song> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM songs WHERE id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<Song> findAll() {
        return jdbcTemplate.query("SELECT * FROM songs", ROW_MAPPER);
    }

    @Override
    public int save(Song song) {
        return jdbcTemplate.update(
                "INSERT INTO songs (title, duration, track_number, album_id, artist_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                song.getTitle(),
                song.getDuration(),
                song.getTrackNumber(),
                song.getAlbumId(),
                song.getArtistId(),
                song.getCreatedAt(),
                song.getUpdatedAt()
        );
    }

    @Override
    public int update(Song song) {
        return jdbcTemplate.update(
                "UPDATE songs SET title = ?, duration = ?, track_number = ?, album_id = ?, artist_id = ?, created_at = ?, updated_at = ? WHERE id = ?",
                song.getTitle(),
                song.getDuration(),
                song.getTrackNumber(),
                song.getAlbumId(),
                song.getArtistId(),
                song.getCreatedAt(),
                song.getUpdatedAt(),
                song.getId()
        );
    }

    @Override
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM songs WHERE id = ?", id);
    }
}
