package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.ArtistReview;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ArtistReviewJdbcDao implements ArtistReviewDao {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<ArtistReview> ROW_MAPPER = (rs, rowNum) -> new ArtistReview(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getLong("artist_id"),
            rs.getString("content"),
            rs.getInt("rating"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getObject("updated_at", LocalDateTime.class)
    );

    public ArtistReviewJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Optional<ArtistReview> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM artist_reviews WHERE id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<ArtistReview> findAll() {
        return jdbcTemplate.query("SELECT * FROM artist_reviews", ROW_MAPPER);
    }

    @Override
    public int save(ArtistReview artistReview) {
        return jdbcTemplate.update(
                "INSERT INTO artist_reviews (user_id, artist_id, content, rating, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)",
                artistReview.getUserId(),
                artistReview.getArtistId(),
                artistReview.getContent(),
                artistReview.getRating(),
                artistReview.getCreatedAt(),
                artistReview.getUpdatedAt()
        );
    }

    @Override
    public int update(ArtistReview artistReview) {
        return jdbcTemplate.update(
                "UPDATE artist_reviews SET user_id = ?, artist_id = ?, content = ?, rating = ?, created_at = ?, updated_at = ? WHERE id = ?",
                artistReview.getUserId(),
                artistReview.getArtistId(),
                artistReview.getContent(),
                artistReview.getRating(),
                artistReview.getCreatedAt(),
                artistReview.getUpdatedAt(),
                artistReview.getId()
        );
    }

    @Override
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM artist_reviews WHERE id = ?", id);
    }
}
