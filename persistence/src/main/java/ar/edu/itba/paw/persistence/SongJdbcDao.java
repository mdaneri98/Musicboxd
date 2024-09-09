package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Song;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class SongJdbcDao implements SongDao {

    private final JdbcTemplate jdbcTemplate;


    public SongJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Optional<Song> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM song WHERE id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                SimpleRowMappers.SONG_ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<Song> findByArtistId(long id) {
        return jdbcTemplate.query("SELECT DISTINCT s.* FROM song s JOIN song_artist sa ON s.id = sa.song_id WHERE sa.artist_id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                SimpleRowMappers.SONG_ROW_MAPPER
        );
    }

    @Override
    public List<Song> findByAlbumId(long id) {
        return jdbcTemplate.query("SELECT * FROM song WHERE album_id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                SimpleRowMappers.SONG_ROW_MAPPER
        );
    }

    @Override
    public List<Song> findAll() {
        return jdbcTemplate.query("SELECT * FROM song", SimpleRowMappers.SONG_ROW_MAPPER);
    }

    @Override
    public int save(Song song) {
        return jdbcTemplate.update(
                "INSERT INTO song (title, duration, track_number, album_id) VALUES (?, ?, ?, ?)",
                song.getTitle(),
                song.getDuration(),
                song.getTrackNumber(),
                song.getAlbumId()
        );
    }

    @Override
    public int update(Song song) {
        return jdbcTemplate.update(
                "UPDATE song SET title = ?, duration = ?, track_number = ?, created_at = ?, updated_at = ?, album_id = ? WHERE id = ?",
                song.getTitle(),
                song.getDuration(),
                song.getTrackNumber(),
                song.getCreatedAt(),
                song.getUpdatedAt(),
                song.getAlbumId(),
                song.getId()
        );
    }

    @Override
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM song WHERE id = ?", id);
    }
}
