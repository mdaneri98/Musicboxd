package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.AlbumReview;
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
            rs.getLong("userId"),
            rs.getLong("albumId"),
            rs.getString("content"),
            rs.getInt("rating"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getObject("updated_at", LocalDateTime.class)
    );

    public AlbumReviewJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Optional<AlbumReview> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM album_reviews WHERE id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<AlbumReview> findAll() {
        return jdbcTemplate.query("SELECT * FROM album_reviews", ROW_MAPPER);
    }

    @Override
    public int save(AlbumReview albumReview) {
        return jdbcTemplate.update(
                "INSERT INTO album_reviews (userId, albumId, content, rating, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)",
                albumReview.getUserId(),
                albumReview.getAlbumId(),
                albumReview.getContent(),
                albumReview.getRating(),
                albumReview.getCreatedAt(),
                albumReview.getUpdatedAt()
        );
    }

    @Override
    public int update(AlbumReview albumReview) {
        return jdbcTemplate.update(
                "UPDATE album_reviews SET userId = ?, albumId = ?, content = ?, rating = ?, created_at = ?, updated_at = ? WHERE id = ?",
                albumReview.getUserId(),
                albumReview.getAlbumId(),
                albumReview.getContent(),
                albumReview.getRating(),
                albumReview.getCreatedAt(),
                albumReview.getUpdatedAt(),
                albumReview.getId()
        );
    }

    @Override
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM album_reviews WHERE id = ?", id);
    }
}
