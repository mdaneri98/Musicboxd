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
        return jdbcTemplate.query("SELECT song.id AS song_id, song.title AS song_title, duration, track_number, song.created_at AS song_created_at, song.updated_at AS song_updated_at, album.id AS album_id, album.title AS album_title, album.img_id AS album_img_id, album.release_date AS album_release_date, album.genre,artist.id AS artist_id, name, artist.img_id AS artist_img_id FROM song JOIN album ON song.album_id = album.id JOIN artist ON album.artist_id = artist.id WHERE song.id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                SimpleRowMappers.SONG_ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<Song> findByArtistId(long id) {
        return jdbcTemplate.query("SELECT DISTINCT song.id AS song_id, song.title AS song_title, duration, track_number, song.created_at AS song_created_at, song.updated_at AS song_updated_at, album.id AS album_id, album.title AS album_title, album.img_id AS album_img_id, album.release_date AS album_release_date, album.genre,artist.id AS artist_id, name, artist.img_id AS artist_img_id FROM song JOIN album ON song.album_id = album.id JOIN artist ON album.artist_id = artist.id LEFT JOIN song_artist ON song.id = song_artist.song_id WHERE song_artist.artist_id = ?;",
                new Object[]{id},
                new int[]{Types.BIGINT},
                SimpleRowMappers.SONG_ROW_MAPPER
        );
    }

    @Override
    public List<Song> findByAlbumId(long id) {
        return jdbcTemplate.query("SELECT song.id AS song_id, song.title AS song_title, duration, track_number, song.created_at AS song_created_at, song.updated_at AS song_updated_at, album.id AS album_id, album.release_date AS album_release_date, album.title AS album_title, album.img_id AS album_img_id, album.genre,artist.id AS artist_id, name, artist.img_id AS artist_img_id FROM song JOIN album ON song.album_id = album.id JOIN artist ON album.artist_id = artist.id WHERE song.album_id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                SimpleRowMappers.SONG_ROW_MAPPER
        );
    }

    @Override
    public List<Song> findAll() {
        return jdbcTemplate.query("SELECT song.id AS song_id, song.title AS song_title, duration, track_number, song.created_at AS song_created_at, song.updated_at AS song_updated_at, album.id AS album_id, album.release_date AS album_release_date, album.title AS album_title, album.img_id AS album_img_id, album.genre,artist.id AS artist_id, name, artist.img_id AS artist_img_id FROM song JOIN album ON song.album_id = album.id JOIN artist ON album.artist_id = artist.id;", SimpleRowMappers.SONG_ROW_MAPPER);
    }

    @Override
    public int save(Song song) {
        return jdbcTemplate.update(
                "INSERT INTO song (title, duration, track_number, album_id) VALUES (?, ?, ?, ?)",
                song.getTitle(),
                song.getDuration(),
                song.getTrackNumber(),
                song.getAlbum().getId()
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
                song.getAlbum().getId(),
                song.getId()
        );
    }

    @Override
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM song WHERE id = ?", id);
    }
}
