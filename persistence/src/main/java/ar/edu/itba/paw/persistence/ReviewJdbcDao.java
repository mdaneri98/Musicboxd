package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.services.ReviewService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ReviewJdbcDao implements ReviewDao {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<ArtistReview> ARTIST_REVIEW_ROW_MAPPER = (rs, rowNum) -> new ArtistReview(
            rs.getLong("id"),
            new Artist(
                    rs.getLong("artist_id"),
                    rs.getString("artist_name"),
                    rs.getLong("artist_img_id")
            ),
            new User(
                    rs.getLong("user_id"),
                    rs.getString("username"),
                    rs.getString("user_name"),
                    rs.getLong("user_img_id")
            ),
            rs.getString("title"),
            rs.getString("description"),
            rs.getInt("rating"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getInt("likes")
    );

    private static final RowMapper<AlbumReview> ALBUM_REVIEW_ROW_MAPPER = (rs, rowNum) -> new AlbumReview(
            rs.getLong("id"),
            new User(
                    rs.getLong("user_id"),
                    rs.getString("username"),
                    rs.getString("user_name"),
                    rs.getLong("user_img_id")
            ),
            new Album(
                    rs.getLong("album_id"),
                    rs.getString("album_title"),
                    rs.getLong("album_img_id"),
                    new Artist(
                            rs.getLong("album_artist_id")
                    ),
                    rs.getObject("album_release_date", LocalDate.class)
            ),
            rs.getString("title"),
            rs.getString("description"),
            rs.getInt("rating"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getInt("likes")
    );

    private static final RowMapper<SongReview> SONG_REVIEW_ROW_MAPPER = (rs, rowNum) -> new SongReview(
            rs.getLong("id"),
            new User(
                    rs.getLong("user_id"),
                    rs.getString("username"),
                    rs.getString("user_name"),
                    rs.getLong("user_img_id")
            ),
            new Song(
                    rs.getLong("song_id"),
                    rs.getString("song_title"),
                    rs.getString("duration"),
                    new Album(
                            rs.getLong("album_id"),
                            rs.getString("album_title"),
                            rs.getLong("album_img_id"),
                            new Artist(
                                    rs.getLong("album_artist_id")
                            ),
                            rs.getObject("album_release_date", LocalDate.class)
                    )
            ),
            rs.getString("title"),
            rs.getString("description"),
            rs.getInt("rating"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getInt("likes")
    );

    public ReviewJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public int update(Review review) {
        return jdbcTemplate.update(
                "UPDATE review SET title = ?, description = ?, rating = ?, likes = ? WHERE id = ?",
                review.getTitle(),
                review.getDescription(),
                review.getRating(),
                review.getLikes(),
                review.getId()
        );
    }

    @Override
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM review WHERE id = ?", id);
    }


    @Override
    public Optional<ArtistReview> findArtistReviewById(long id) {
        return jdbcTemplate.query(
                "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, a.id AS artist_id, a.name AS artist_name, a.img_id AS artist_img_id " +
                        "FROM review r " +
                        "JOIN cuser u ON r.user_id = u.id " +
                        "JOIN artist_review ar ON r.id = ar.review_id " +
                        "JOIN artist a ON ar.artist_id = a.id " +
                        "WHERE r.id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                ARTIST_REVIEW_ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<ArtistReview> findReviewsByArtistId(long artistId) {
        return jdbcTemplate.query(
                "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, a.id AS artist_id, a.name AS artist_name, a.img_id AS artist_img_id " +
                        "FROM review r " +
                        "JOIN cuser u ON r.user_id = u.id " +
                        "JOIN artist_review ar ON r.id = ar.review_id " +
                        "JOIN artist a ON ar.artist_id = a.id " +
                        "WHERE a.id = ?",
                new Object[]{artistId},
                new int[]{Types.BIGINT},
                ARTIST_REVIEW_ROW_MAPPER
        );
    }

    @Override
    public int saveArtistReview(ArtistReview review) {
        int result = 0;
        KeyHolder keyHolder = saveReviewWithNoId(review);
        if (keyHolder != null ) {
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("id")) {
                long reviewId = ((Number) keys.get("id")).longValue();
                jdbcTemplate.update(
                        "INSERT INTO artist_review (review_id, artist_id) VALUES (?, ?)",
                        reviewId,
                        review.getArtist().getId()
                );
                review.setId(reviewId);
            } else {
                throw new RuntimeException("Failed to retrieve the generated review ID");
            }
        }
        return result;
    }

    @Override
    public Optional<AlbumReview> findAlbumReviewById(long id) {
        return jdbcTemplate.query(
                "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, al.id AS album_id, al.title AS album_title, al.img_id AS album_img_id, al.artist_id AS album_artist_id, al.release_date AS album_release_date " +
                        "FROM review r " +
                        "JOIN cuser u ON r.user_id = u.id " +
                        "JOIN album_review ar ON r.id = ar.review_id " +
                        "JOIN album al ON ar.album_id = al.id " +
                        "WHERE r.id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                ALBUM_REVIEW_ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<AlbumReview> findReviewsByAlbumId(long albumId) {
        return jdbcTemplate.query(
                "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, al.id AS album_id, al.title AS album_title, al.img_id AS album_img_id, al.artist_id AS album_artist_id, al.release_date AS album_release_date " +
                        "FROM review r " +
                        "JOIN cuser u ON r.user_id = u.id " +
                        "JOIN album_review ar ON r.id = ar.review_id " +
                        "JOIN album al ON ar.album_id = al.id " +
                        "WHERE al.id = ?",
                new Object[]{albumId},
                new int[]{Types.BIGINT},
                ALBUM_REVIEW_ROW_MAPPER
        );
    }

    @Override
    public int saveAlbumReview(AlbumReview review) {
        int result = 0;
        KeyHolder keyHolder = saveReviewWithNoId(review);
        if (keyHolder != null ) {
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("id")) {
                long reviewId = ((Number) keys.get("id")).longValue();
                jdbcTemplate.update(
                        "INSERT INTO album_review (review_id, album_id) VALUES (?, ?)",
                        reviewId,
                        review.getAlbum().getId()
                );
                review.setId(reviewId);
            } else {
                throw new RuntimeException("Failed to retrieve the generated review ID");
            }
        }
        return result;
    }


    @Override
    public Optional<SongReview> findSongReviewById(long id) {
        return jdbcTemplate.query(
                "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, " +
                        "s.id AS song_id, s.title AS song_title, s.duration, " +
                        "al.id AS album_id, al.title AS album_title, al.img_id AS album_img_id, al.artist_id AS album_artist_id, al.release_date AS album_release_date " +
                        "FROM review r " +
                        "JOIN cuser u ON r.user_id = u.id " +
                        "JOIN song_review sr ON r.id = sr.review_id " +
                        "JOIN song s ON sr.song_id = s.id " +
                        "JOIN album al ON s.album_id = al.id " +
                        "WHERE r.id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                SONG_REVIEW_ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<SongReview> findReviewsBySongId(long songId) {
        return jdbcTemplate.query(
                "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, " +
                        "s.id AS song_id, s.title AS song_title, s.duration, " +
                        "al.id AS album_id, al.title AS album_title, al.img_id AS album_img_id, al.artist_id AS album_artist_id, al.release_date AS album_release_date " +
                        "FROM review r " +
                        "JOIN cuser u ON r.user_id = u.id " +
                        "JOIN song_review sr ON r.id = sr.review_id " +
                        "JOIN song s ON sr.song_id = s.id " +
                        "JOIN album al ON s.album_id = al.id " +
                        "WHERE s.id = ?",
                new Object[]{songId},
                new int[]{Types.BIGINT},
                SONG_REVIEW_ROW_MAPPER
        );
    }

    @Override
    public int saveSongReview(SongReview review) {
        int result = 0;
        KeyHolder keyHolder = saveReviewWithNoId(review);
        if (keyHolder != null ) {
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("id")) {
                long reviewId = ((Number) keys.get("id")).longValue();
                jdbcTemplate.update(
                        "INSERT INTO song_review (review_id, song_id) VALUES (?, ?)",
                        reviewId,
                        review.getSong().getId()
                );
                review.setId(reviewId);
            } else {
                throw new RuntimeException("Failed to retrieve the generated review ID");
            }
        }
        return result;
    }

    @Override
    public List<SongReview> findSongReviewsPaginated(long songId, int page, int pageSize) {
        return jdbcTemplate.query(
                "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, " +
                        "s.id AS song_id, s.title AS song_title, s.duration, " +
                        "al.id AS album_id, al.title AS album_title, al.img_id AS album_img_id, al.artist_id AS album_artist_id, al.release_date AS album_release_date " +
                        "FROM review r " +
                        "JOIN cuser u ON r.user_id = u.id " +
                        "JOIN song_review sr ON r.id = sr.review_id " +
                        "JOIN song s ON sr.song_id = s.id " +
                        "JOIN album al ON s.album_id = al.id " +
                        "WHERE s.id = ? " +
                        "ORDER BY r.created_at DESC LIMIT ? OFFSET ?",
                new Object[]{songId, pageSize, (page - 1) * pageSize},
                new int[]{Types.BIGINT, Types.INTEGER, Types.INTEGER},
                SONG_REVIEW_ROW_MAPPER
        );
    }

    @Override
    public int createLike(long userId, long reviewId) {
        return jdbcTemplate.update("INSERT INTO review_like (user_id, review_id) VALUES (?, ?)", userId, reviewId);
    }

    @Override
    public int deleteLike(long userId, long reviewId) {
        return jdbcTemplate.update("DELETE FROM review_like WHERE user_id = ? AND review_id = ?", userId, reviewId);
    }

    @Override
    public int incrementLikes(long reviewId) {
        return jdbcTemplate.update("UPDATE review SET likes = likes + 1 WHERE id = ?", reviewId);
    }

    @Override
    public int decrementLikes(long reviewId) {
        return jdbcTemplate.update("UPDATE review SET likes = likes - 1 WHERE id = ?", reviewId);
    }

    @Override
    public boolean isLiked(Long userId, Long reviewId) {
        String sql = "SELECT COUNT(*) FROM review_like WHERE user_id = ? AND review_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, userId, reviewId);
        return count > 0;
    }

    @Override
    public List<ArtistReview> findArtistReviewsPaginated(long artistId, int page, int pageSize) {
        return jdbcTemplate.query(
                "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, a.id AS artist_id, a.name AS artist_name, a.img_id AS artist_img_id " +
                        "FROM review r " +
                        "JOIN cuser u ON r.user_id = u.id " +
                        "JOIN artist_review ar ON r.id = ar.review_id " +
                        "JOIN artist a ON ar.artist_id = a.id " +
                        "WHERE a.id = ? " +
                        "ORDER BY r.created_at DESC LIMIT ? OFFSET ?",
                new Object[]{artistId, pageSize, (page - 1) * pageSize},
                new int[]{Types.BIGINT, Types.INTEGER, Types.INTEGER},
                ARTIST_REVIEW_ROW_MAPPER
        );
    }

    @Override
    public List<AlbumReview> findAlbumReviewsPaginated(long albumId, int page, int pageSize) {
        return jdbcTemplate.query(
                "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, al.id AS album_id, al.title AS album_title, al.img_id AS album_img_id, al.artist_id AS album_artist_id, al.release_date AS album_release_date " +
                        "FROM review r " +
                        "JOIN cuser u ON r.user_id = u.id " +
                        "JOIN album_review ar ON r.id = ar.review_id " +
                        "JOIN album al ON ar.album_id = al.id " +
                        "WHERE al.id = ? " +
                        "ORDER BY r.created_at DESC LIMIT ? OFFSET ?",
                new Object[]{albumId, pageSize, (page - 1) * pageSize},
                new int[]{Types.BIGINT, Types.INTEGER, Types.INTEGER},
                ALBUM_REVIEW_ROW_MAPPER
        );
    }

    public KeyHolder saveReviewWithNoId(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int result = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO review (user_id, title, description, rating, created_at, likes) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, review.getUser().getId());
            ps.setString(2, review.getTitle());
            ps.setString(3, review.getDescription());
            ps.setInt(4, review.getRating());
            ps.setObject(5, review.getCreatedAt());
            ps.setInt(6, review.getLikes());
            return ps;
        }, keyHolder);
        if (result == 0) return null;
        return keyHolder;
    }

    @Override
    public List<ArtistReview> findArtistReviewsByUser(long userId) {
        final String sql = "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, a.id AS artist_id, a.name AS artist_name, a.img_id AS artist_img_id " +
                "FROM review r " +
                "JOIN cuser u ON r.user_id = u.id " +
                "JOIN artist_review ar ON r.id = ar.review_id " +
                "JOIN artist a ON ar.artist_id = a.id " +
                "WHERE r.user_id = ? " +
                "ORDER BY r.created_at";

        return jdbcTemplate.query(sql, new Object[]{userId}, ARTIST_REVIEW_ROW_MAPPER);
    }

    @Override
    public List<AlbumReview> findAlbumReviewsByUser(long userId) {
        final String sql = "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, al.id AS album_id, al.title AS album_title, al.img_id AS album_img_id, al.artist_id AS album_artist_id, al.release_date AS album_release_date " +
                "FROM review r " +
                "JOIN cuser u ON r.user_id = u.id " +
                "JOIN album_review ar ON r.id = ar.review_id " +
                "JOIN album al ON ar.album_id = al.id " +
                "WHERE r.user_id = ? " +
                "ORDER BY r.created_at";

        return jdbcTemplate.query(sql, new Object[]{userId}, ALBUM_REVIEW_ROW_MAPPER);
    }

    @Override
    public List<SongReview> findSongReviewsByUser(long userId) {
        final String sql = "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, " +
                "s.id AS song_id, s.title AS song_title, s.duration, " +
                "al.id AS album_id, al.title AS album_title, al.img_id AS album_img_id, al.artist_id AS album_artist_id, al.release_date AS album_release_date " +
                "FROM review r " +
                "JOIN cuser u ON r.user_id = u.id " +
                "JOIN song_review sr ON r.id = sr.review_id " +
                "JOIN song s ON sr.song_id = s.id " +
                "JOIN album al ON s.album_id = al.id " +
                "WHERE r.user_id = ? " +
                "ORDER BY r.created_at";

        return jdbcTemplate.query(sql, new Object[]{userId}, SONG_REVIEW_ROW_MAPPER);
    }

    @Override
    public List<SongReview> findPopularSongReviewsSince(LocalDate date) {
        final String sql = "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, " +
                "s.id AS song_id, s.title AS song_title, s.duration, " +
                "al.id AS album_id, al.title AS album_title, al.img_id AS album_img_id, al.artist_id AS album_artist_id, al.release_date AS album_release_date " +
                "FROM review r " +
                "JOIN cuser u ON r.user_id = u.id " +
                "JOIN song_review sr ON r.id = sr.review_id " +
                "JOIN song s ON sr.song_id = s.id " +
                "JOIN album al ON s.album_id = al.id " +
                "WHERE r.created_at >= ? " +
                "ORDER BY r.likes DESC LIMIT 30";

        return jdbcTemplate.query(sql, new Object[]{date}, SONG_REVIEW_ROW_MAPPER);
    }

    @Override
    public List<AlbumReview> findPopularAlbumReviewsSince(LocalDate date) {
        final String sql = "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, al.id AS album_id, al.title AS album_title, al.img_id AS album_img_id, al.artist_id AS album_artist_id, al.release_date AS album_release_date " +
                "FROM review r " +
                "JOIN cuser u ON r.user_id = u.id " +
                "JOIN album_review ar ON r.id = ar.review_id " +
                "JOIN album al ON ar.album_id = al.id " +
                "WHERE r.created_at >= ? " +
                "ORDER BY r.likes DESC LIMIT 30";

        return jdbcTemplate.query(sql, new Object[]{date}, ALBUM_REVIEW_ROW_MAPPER);
    }

    @Override
    public List<ArtistReview> findPopularArtistReviewsSince(LocalDate date) {
        final String sql = "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, a.id AS artist_id, a.name AS artist_name, a.img_id AS artist_img_id " +
                "FROM review r " +
                "JOIN cuser u ON r.user_id = u.id " +
                "JOIN artist_review ar ON r.id = ar.review_id " +
                "JOIN artist a ON ar.artist_id = a.id " +
                "WHERE r.created_at >= ? " +
                "ORDER BY r.likes DESC LIMIT 30";

        return jdbcTemplate.query(sql, new Object[]{date}, ARTIST_REVIEW_ROW_MAPPER);
    }

    @Override
    public List<SongReview> findSongReviewsFromFollowedUsers(Long userId) {
        final String sql = "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, " +
                "s.id AS song_id, s.title AS song_title, s.duration, " +
                "al.id AS album_id, al.title AS album_title, al.img_id AS album_img_id, al.artist_id AS album_artist_id, al.release_date AS album_release_date " +
                "FROM review r " +
                "JOIN follower f ON r.user_id = f.following " +
                "JOIN cuser u ON r.user_id = u.id " +
                "JOIN song_review sr ON r.id = sr.review_id " +
                "JOIN song s ON sr.song_id = s.id " +
                "JOIN album al ON s.album_id = al.id " +
                "WHERE f.user_id = ? " +
                "ORDER BY r.created_at DESC LIMIT 20";

        return jdbcTemplate.query(sql, new Object[]{userId}, SONG_REVIEW_ROW_MAPPER);
    }

    @Override
    public boolean isArtistReview(long reviewId) {
        String sql = "SELECT COUNT(*) FROM artist_review WHERE review_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, reviewId);
        return count > 0;
    }

    @Override
    public boolean isAlbumReview(long reviewId) {
        String sql = "SELECT COUNT(*) FROM album_review WHERE review_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, reviewId);
        return count > 0;
    }

    @Override
    public boolean isSongReview(long reviewId) {
        String sql = "SELECT COUNT(*) FROM song_review WHERE review_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, reviewId);
        return count > 0;
    }

    @Override
    public List<AlbumReview> findAlbumReviewsFromFollowedUsers(Long userId) {
        final String sql = "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, al.id AS album_id, al.title AS album_title, al.img_id AS album_img_id, al.artist_id AS album_artist_id, al.release_date AS album_release_date " +
                "FROM review r " +
                "JOIN follower f ON r.user_id = f.following " +
                "JOIN cuser u ON r.user_id = u.id " +
                "JOIN album_review ar ON r.id = ar.review_id " +
                "JOIN album al ON ar.album_id = al.id " +
                "WHERE f.user_id = ? " +
                "ORDER BY r.created_at DESC LIMIT 20";

        return jdbcTemplate.query(sql, new Object[]{userId}, ALBUM_REVIEW_ROW_MAPPER);
    }

    @Override
    public List<ArtistReview> findArtistReviewsFromFollowedUsers(Long userId) {
        final String sql = "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, a.id AS artist_id, a.name AS artist_name, a.img_id AS artist_img_id " +
                "FROM review r " +
                "JOIN follower f ON r.user_id = f.following " +
                "JOIN cuser u ON r.user_id = u.id " +
                "JOIN artist_review ar ON r.id = ar.review_id " +
                "JOIN artist a ON ar.artist_id = a.id " +
                "WHERE f.user_id = ? " +
                "ORDER BY r.created_at DESC LIMIT 20";

        return jdbcTemplate.query(sql, new Object[]{userId}, ARTIST_REVIEW_ROW_MAPPER);
    }
}