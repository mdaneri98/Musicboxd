package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class SongJdbcDao implements SongDao {

    private final JdbcTemplate jdbcTemplate;


    public SongJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Optional<Song> findById(long id) {
        return jdbcTemplate.query("SELECT song.id AS song_id, song.title AS song_title, duration, track_number, song.created_at AS song_created_at, song.updated_at AS song_updated_at, song.avg_rating AS avg_rating, song.rating_amount AS rating_amount, album.id AS album_id, album.title AS album_title, album.img_id AS album_img_id, album.release_date AS album_release_date, album.genre,artist.id AS artist_id, name, artist.img_id AS artist_img_id FROM song JOIN album ON song.album_id = album.id JOIN artist ON album.artist_id = artist.id WHERE song.id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                SimpleRowMappers.SONG_ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<Song> findByArtistId(long id) {
        return jdbcTemplate.query("SELECT DISTINCT song.id AS song_id, song.title AS song_title, duration, track_number, song.created_at AS song_created_at, song.updated_at AS song_updated_at, song.avg_rating AS avg_rating, song.rating_amount AS rating_amount, album.id AS album_id, album.title AS album_title, album.img_id AS album_img_id, album.release_date AS album_release_date, album.genre,artist.id AS artist_id, name, artist.img_id AS artist_img_id FROM song JOIN album ON song.album_id = album.id JOIN artist ON album.artist_id = artist.id LEFT JOIN song_artist ON song.id = song_artist.song_id WHERE song_artist.artist_id = ? ORDER BY song_created_at LIMIT 10",
                new Object[]{id},
                new int[]{Types.BIGINT},
                SimpleRowMappers.SONG_ROW_MAPPER
        );
    }

    @Override
    public List<Song> findByAlbumId(long id) {
        return jdbcTemplate.query("SELECT song.id AS song_id, song.title AS song_title, duration, track_number, song.created_at AS song_created_at, song.updated_at AS song_updated_at, song.avg_rating AS avg_rating, song.rating_amount AS rating_amount, album.id AS album_id, album.release_date AS album_release_date, album.title AS album_title, album.img_id AS album_img_id, album.genre,artist.id AS artist_id, name, artist.img_id AS artist_img_id FROM song JOIN album ON song.album_id = album.id JOIN artist ON album.artist_id = artist.id WHERE song.album_id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                SimpleRowMappers.SONG_ROW_MAPPER
        );
    }

    @Override
    public List<Song> findByTitleContaining(String sub) {
        String sql = "SELECT song.id AS song_id, song.title AS song_title, duration, track_number, song.created_at AS song_created_at, song.updated_at AS song_updated_at, song.avg_rating AS avg_rating, song.rating_amount AS rating_amount, album.id AS album_id, album.title AS album_title, album.img_id AS album_img_id, album.release_date AS album_release_date, album.genre,artist.id AS artist_id, name, artist.img_id AS artist_img_id FROM song JOIN album ON song.album_id = album.id JOIN artist ON album.artist_id = artist.id" +
                 "WHERE title ILIKE ? LIMIT 10";

        return jdbcTemplate.query(sql, new Object[]{"%" + sub + "%"}, SimpleRowMappers.SONG_ROW_MAPPER);
    }

    @Override
    public List<Song> findAll() {
        return jdbcTemplate.query("SELECT song.id AS song_id, song.title AS song_title, duration, track_number, song.created_at AS song_created_at, song.updated_at AS song_updated_at, song.avg_rating AS avg_rating, song.rating_amount AS rating_amount, album.id AS album_id, album.release_date AS album_release_date, album.title AS album_title, album.img_id AS album_img_id, album.genre,artist.id AS artist_id, name, artist.img_id AS artist_img_id FROM song JOIN album ON song.album_id = album.id JOIN artist ON album.artist_id = artist.id;", SimpleRowMappers.SONG_ROW_MAPPER);
    }

    @Override
    public List<Song> findPaginated(FilterType filterType, int limit, int offset) {
        String sql = "SELECT song.id AS song_id, song.title AS song_title, duration, track_number, song.created_at AS song_created_at, song.updated_at AS song_updated_at, song.avg_rating AS avg_rating, song.rating_amount AS rating_amount, album.id AS album_id, album.title AS album_title, album.img_id AS album_img_id, album.release_date AS album_release_date, album.genre,artist.id AS artist_id, name, artist.img_id AS artist_img_id FROM song JOIN album ON song.album_id = album.id JOIN artist ON album.artist_id = artist.id" +
                filterType.getFilter() +
                "LIMIT ? OFFSET ?";

        return jdbcTemplate.query(sql, new Object[]{ limit, offset }, new int[]{ Types.BIGINT, Types.BIGINT }, SimpleRowMappers.SONG_ROW_MAPPER);
    }

    @Override
    public long save(Song song) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int result = jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO song (title, duration, track_number, album_id) VALUES (?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setString(1, song.getTitle());
                    ps.setString(2, song.getDuration());
                    ps.setInt(3, song.getTrackNumber());
                    ps.setLong(4,song.getAlbum().getId());
                    return ps;
                }, keyHolder);
        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null && keys.containsKey("id")) {
            return ((Number) keys.get("id")).longValue();
        } else {
            throw new IllegalStateException("Failed to insert album or generate key.");
        }
    }

    public int saveSongArtist(Song song, Artist artist) {
        return jdbcTemplate.update(
                    "INSERT INTO song_artist (song_id, artist_id) VALUES (?, ?)",
                song.getId(),
                artist.getId()
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
    public void updateRating(long songId, float newRating, int newRatingAmount) {
        final String sql = "UPDATE song SET avg_rating = ?, rating_amount = ? WHERE id = ?";
        jdbcTemplate.update(sql, newRating, newRatingAmount, songId);
    }

    @Override
    public boolean hasUserReviewed(long userId, long songId) {
        final String sql = "SELECT COUNT(*) FROM song_review sr JOIN review r ON sr.review_id = r.id WHERE r.user_id = ? AND sr.song_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, userId, songId);
        return count > 0;
    }

    @Override
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM song WHERE id = ?", id);
    }
}
