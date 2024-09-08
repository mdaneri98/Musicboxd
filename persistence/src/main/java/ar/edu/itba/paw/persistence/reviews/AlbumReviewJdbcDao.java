package ar.edu.itba.paw.persistence.reviews;

import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.persistence.AlbumReviewDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class AlbumReviewJdbcDao implements AlbumReviewDao {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<AlbumReview> ROW_MAPPER = (rs, rowNum) -> new AlbumReview(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getLong("album_id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getInt("rating"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getInt("likes")
    );

    public AlbumReviewJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Optional<AlbumReview> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM album_review WHERE id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<AlbumReview> findAll() {
        return jdbcTemplate.query("SELECT * FROM album_review", ROW_MAPPER);
    }

    @Override
    public List<AlbumReview> findByAlbumId(long id){
        return jdbcTemplate.query("SELECT * FROM album_review WHERE album_id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                ROW_MAPPER
        );
    }

    @Override
    public int save(AlbumReview albumReview) {
        return jdbcTemplate.update(
                "INSERT INTO album_review (user_id, album_id, title, description, rating, created_at, likes) VALUES (?, ?, ?, ?, ?, ?, ?)",
                albumReview.getUserId(),
                albumReview.getAlbumId(),
                albumReview.getTitle(),
                albumReview.getDescription(),
                albumReview.getRating(),
                albumReview.getCreatedAt(),
                albumReview.getLikes()
        );
    }

    @Override
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM album_review WHERE id = ?", id);
    }
}
