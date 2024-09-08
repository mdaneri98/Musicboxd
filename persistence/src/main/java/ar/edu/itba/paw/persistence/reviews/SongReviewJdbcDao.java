package ar.edu.itba.paw.persistence.reviews;

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
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getLong("artist_id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getInt("rating"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getInt("likes")
    );

    public SongReviewJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Optional<SongReview> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM song_reviews WHERE id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<SongReview> findAll() {
        return jdbcTemplate.query("SELECT * FROM song_reviews", ROW_MAPPER);
    }

    @Override
    public int save(SongReview songReview) {
        return jdbcTemplate.update(
                "INSERT INTO song_reviews (user_id, song_id, title, description, rating, created_at, likes) VALUES (?, ?, ?, ?, ?, ?, ?)",
                songReview.getUserId(),
                songReview.getSongId(),
                songReview.getTitle(),
                songReview.getDescription(),
                songReview.getRating(),
                songReview.getCreatedAt(),
                songReview.getLikes()
        );
    }

    @Override
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM song_reviews WHERE id = ?", id);
    }
}
