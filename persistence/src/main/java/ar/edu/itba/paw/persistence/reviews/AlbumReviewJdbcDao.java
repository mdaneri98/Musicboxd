package ar.edu.itba.paw.persistence.reviews;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.User;
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
            rs.getLong("album_review.id"),
            new User(
                    rs.getLong("cuser.id"),
                    rs.getString("username"),
                    rs.getString("name"),
                    rs.getLong("cuser.img_id")
            ),
            new Album(
                    rs.getLong("album.id"),
                    rs.getString("album.title"),
                    rs.getLong("album.img_id")
            ),
            rs.getString("album_review.title"),
            rs.getString("description"),
            rs.getInt("rating"),
            rs.getObject("album_review.created_at", LocalDateTime.class),
            rs.getInt("likes")
    );

    public AlbumReviewJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Optional<AlbumReview> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM album_review JOIN cuser ON album_review.user_id = cuser.id JOIN album ON album_review.album_id = album.id WHERE album_review.id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<AlbumReview> findAll() {
        return jdbcTemplate.query("SELECT * FROM album_review JOIN cuser ON album_review.user_id = cuser.id JOIN album ON album_review.album_id = album.id", ROW_MAPPER);
    }

    @Override
    public List<AlbumReview> findByAlbumId(long id){
        return jdbcTemplate.query("SELECT * FROM album_review JOIN cuser ON album_review.user_id = cuser.id JOIN album ON album_review.album_id = album.id JOIN cuser ON album_review.user_id = cuser.id JOIN album ON album_review.album_id = album.id WHERE album_review.album_id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                ROW_MAPPER
        );
    }

    @Override
    public int save(AlbumReview albumReview) {
        return jdbcTemplate.update(
                "INSERT INTO album_review (user_id, album_id, title, description, rating, created_at, likes) VALUES (?, ?, ?, ?, ?, ?, ?)",
                albumReview.getUser().getId(),
                albumReview.getAlbum().getId(),
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
