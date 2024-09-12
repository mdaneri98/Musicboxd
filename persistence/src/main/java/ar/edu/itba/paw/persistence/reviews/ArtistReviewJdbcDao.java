package ar.edu.itba.paw.persistence.reviews;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.ArtistReview;
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
            rs.getLong("artist_review_id"),
            new Artist(
                    rs.getLong("artist_id"),
                    rs.getString("artist_name"),
                    rs.getLong("artist_img_id")
            ),
            new User(
                    rs.getLong("cuser_id"),
                    rs.getString("username"),
                    rs.getString("cuser_name"),
                    rs.getLong("cuser_img_id")
            ),
            rs.getString("title"),
            rs.getString("description"),
            rs.getInt("rating"),
            rs.getObject("artist_review_created_at", LocalDateTime.class),
            rs.getInt("likes")
    );

    public ArtistReviewJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Optional<ArtistReview> findById(long id) {
        return jdbcTemplate.query("SELECT artist_review.id AS artist_review_id, title, description, rating, artist_review.created_at AS artist_review_created_at, likes, cuser.id AS cuser_id, username, cuser.name AS cuser_name, cuser.img_id AS cuser_img_id, artist.id AS artist_id, artist.name AS artist_name, artist.img_id AS artist_img_id FROM artist_review JOIN cuser ON artist_review.user_id = cuser.id JOIN artist ON artist_review.artist_id = artist.id WHERE artist_review.id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<ArtistReview> findAll() {
        return jdbcTemplate.query("SELECT artist_review.id AS artist_review_id, title, description, rating, artist_review.created_at AS artist_review_created_at, likes, cuser.id AS cuser_id, username, cuser.name AS cuser_name, cuser.img_id AS cuser_img_id, artist.id AS artist_id, artist.name AS artist_name, artist.img_id AS artist_img_id FROM artist_review JOIN cuser ON artist_review.user_id = cuser.id JOIN artist ON artist_review.artist_id = artist.id", ROW_MAPPER);
    }

    public List<ArtistReview> findByArtistId(long id) {
        return jdbcTemplate.query("SELECT artist_review.id AS artist_review_id, title, description, rating, artist_review.created_at AS artist_review_created_at, likes, cuser.id AS cuser_id, username, cuser.name AS cuser_name, cuser.img_id AS cuser_img_id, artist.id AS artist_id, artist.name AS artist_name, artist.img_id AS artist_img_id FROM artist_review JOIN cuser ON artist_review.user_id = cuser.id JOIN artist ON artist_review.artist_id = artist.id WHERE artist_review.artist_id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                ROW_MAPPER
        );
    }

    @Override
    public int save(ArtistReview artistReview) {
        return jdbcTemplate.update(
                "INSERT INTO artist_review (user_id, artist_id, title, description, rating, created_at, likes) VALUES (?, ?, ?, ?, ?, ?, ?)",
                artistReview.getUser().getId(),
                artistReview.getArtist().getId(),
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
