package ar.edu.itba.paw.persistence.reviews;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.persistence.SongReviewDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class SongReviewJdbcDao implements SongReviewDao {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<SongReview> ROW_MAPPER = (rs, rowNum) -> new SongReview(
            rs.getLong("song_review.id"),
            new User(
                    rs.getLong("cuser.id"),
                    rs.getString("username"),
                    rs.getString("name"),
                    rs.getLong("img_id")
            ),
            new Song(
                    rs.getLong("song.id"),
                    rs.getString("song.title"),
                    rs.getString("duration"),
                    new Album(
                            rs.getLong("album.id"),
                            rs.getString("album.title"),
                            rs.getLong("album.img_id")
                    )
            ),
            rs.getString("song_review.title"),
            rs.getString("description"),
            rs.getInt("rating"),
            rs.getObject("song_review.created_at", LocalDateTime.class),
            rs.getInt("likes")
    );

    public SongReviewJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Optional<SongReview> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM song_review JOIN cuser ON song_review.user_id = cuser.id JOIN song ON song_review.song_id = song.id JOIN album ON song.album_id = album.id WHERE song_review.id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<SongReview> findAll() {
        return jdbcTemplate.query("SELECT * FROM song_review JOIN cuser ON song_review.user_id = cuser.id JOIN song ON song_review.song_id = song.id", ROW_MAPPER);
    }

    @Override
    public int save(SongReview songReview) {
        return jdbcTemplate.update(
                "INSERT INTO song_review (user_id, song_id, title, description, rating, created_at, likes) VALUES (?, ?, ?, ?, ?, ?, ?)",
                songReview.getUser().getId(),
                songReview.getSong().getId(),
                songReview.getTitle(),
                songReview.getDescription(),
                songReview.getRating(),
                songReview.getCreatedAt(),
                songReview.getLikes()
        );
    }

    @Override
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM song_review WHERE id = ?", id);
    }
}
