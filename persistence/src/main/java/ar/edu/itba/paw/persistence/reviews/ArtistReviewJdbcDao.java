package ar.edu.itba.paw.persistence.reviews;

import ar.edu.itba.paw.reviews.ArtistReview;
import ar.edu.itba.paw.persistence.ArtistReviewDao;
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
            rs.getString("title"),
            rs.getString("description"),
            rs.getInt("rating"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getInt("likes")
    );

    public ArtistReviewJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Optional<ArtistReview> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM artist_review WHERE id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<ArtistReview> findAll() {
        return jdbcTemplate.query("SELECT * FROM artist_review", ROW_MAPPER);
    }

    @Override
    public int save(ArtistReview artistReview) {
        return jdbcTemplate.update(
                "INSERT INTO artist_review (user_id, artist_id, title, description, rating, created_at, likes) VALUES (?, ?, ?, ?, ?, ?, ?)",
                artistReview.getUserId(),
                artistReview.getArtistId(),
                artistReview.getTitle(),
                artistReview.getDescription(),
                artistReview.getRating(),
                artistReview.getCreatedAt(),
                artistReview.getLikes()
        );
    }

    @Override
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM artist_review WHERE id = ?", id);
    }
}
