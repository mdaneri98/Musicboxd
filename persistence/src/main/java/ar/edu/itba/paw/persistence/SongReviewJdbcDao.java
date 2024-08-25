package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.SongReview;
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
            rs.getLong("userId"),
            rs.getLong("songId"),
            rs.getString("content"),
            rs.getInt("rating"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getObject("updated_at", LocalDateTime.class)
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
                "INSERT INTO song_reviews (userId, songId, content, rating, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)",
                songReview.getUserId(),
                songReview.getSongId(),
                songReview.getContent(),
                songReview.getRating(),
                songReview.getCreatedAt(),
                songReview.getUpdatedAt()
        );
    }

    @Override
    public int update(SongReview songReview) {
        return jdbcTemplate.update(
                "UPDATE song_reviews SET userId = ?, songId = ?, content = ?, rating = ?, created_at = ?, updated_at = ? WHERE id = ?",
                songReview.getUserId(),
                songReview.getSongId(),
                songReview.getContent(),
                songReview.getRating(),
                songReview.getCreatedAt(),
                songReview.getUpdatedAt(),
                songReview.getId()
        );
    }

    @Override
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM song_reviews WHERE id = ?", id);
    }
}
