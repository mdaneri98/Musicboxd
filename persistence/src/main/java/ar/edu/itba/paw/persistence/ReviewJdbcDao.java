package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.SongReview;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
                    rs.getLong("user_img_id"),
                    rs.getBoolean("verified"),
                    rs.getBoolean("moderator")
            ),
            rs.getString("title"),
            rs.getString("description"),
            rs.getInt("rating"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getInt("likes"),
            rs.getBoolean("isBlocked")
    );

    private static final RowMapper<AlbumReview> ALBUM_REVIEW_ROW_MAPPER = (rs, rowNum) -> new AlbumReview(
            rs.getLong("id"),
            new User(
                    rs.getLong("user_id"),
                    rs.getString("username"),
                    rs.getString("user_name"),
                    rs.getLong("user_img_id"),
                    rs.getBoolean("verified"),
                    rs.getBoolean("moderator")
            ),
            new Album(
                    rs.getLong("album_id"),
                    rs.getString("album_title"),
                    rs.getLong("album_img_id"),
                    rs.getString("genre"),
                    new Artist(
                            rs.getLong("album_artist_id")
                    ),
                    rs.getObject("album_release_date", LocalDate.class)
            ),
            rs.getString("title"),
            rs.getString("description"),
            rs.getInt("rating"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getInt("likes"),
            rs.getBoolean("isBlocked")
    );

    private static final RowMapper<SongReview> SONG_REVIEW_ROW_MAPPER = (rs, rowNum) -> new SongReview(
            rs.getLong("id"),
            new User(
                    rs.getLong("user_id"),
                    rs.getString("username"),
                    rs.getString("user_name"),
                    rs.getLong("user_img_id"),
                    rs.getBoolean("verified"),
                    rs.getBoolean("moderator")
            ),
            new Song(
                    rs.getLong("song_id"),
                    rs.getString("song_title"),
                    rs.getString("duration"),
                    new Album(
                            rs.getLong("album_id"),
                            rs.getString("album_title"),
                            rs.getLong("album_img_id"),
                            rs.getString("genre"),
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
            rs.getInt("likes"),
            rs.getBoolean("isBlocked")
    );

    public static class ReviewRowMapper implements RowMapper<Review> {

        @Override
        public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User(
                    rs.getLong("user_id"),
                    rs.getString("username"),
                    rs.getString("user_name"),
                    rs.getLong("user_img_id"),
                    rs.getBoolean("verified"),
                    rs.getBoolean("moderator")
            );

            String title = rs.getString("title");
            String description = rs.getString("description");
            int rating = rs.getInt("rating");
            LocalDateTime createdAt = rs.getObject("created_at", LocalDateTime.class);
            int likes = rs.getInt("likes");
            boolean isBlocked = rs.getBoolean("isBlocked");

            Long songReviewId = getLongOrNull(rs, "song_review_id");
            Long albumReviewId = getLongOrNull(rs, "album_review_id");
            Long artistReviewId = getLongOrNull(rs, "artist_review_id");


            if (songReviewId != null) {
                Song song = new Song(
                        rs.getLong("song_id"),
                        rs.getString("song_title"),
                        rs.getString("duration"),
                        new Album(
                                rs.getLong("album_id"),
                                rs.getString("album_title"),
                                rs.getLong("album_img_id"),
                                rs.getString("genre"),
                                new Artist(rs.getLong("album_artist_id")),
                                rs.getObject("album_release_date", LocalDate.class)
                        )
                );
                return new SongReview(rs.getLong("id"), user, song, title, description, rating, createdAt, likes, isBlocked);
            }
            if (albumReviewId != null) {
                Album album = new Album(
                        rs.getLong("album_id"),
                        rs.getString("album_title"),
                        rs.getLong("album_img_id"),
                        rs.getString("genre"),
                        new Artist(rs.getLong("album_artist_id")),
                        rs.getObject("album_release_date", LocalDate.class)
                );
                return new AlbumReview(rs.getLong("id"), user, album, title, description, rating, createdAt, likes, isBlocked);
            }
            if (artistReviewId != null) {
                Artist artist = new Artist(
                        rs.getLong("artist_id"),
                        rs.getString("artist_name"),
                        rs.getLong("artist_img_id")
                );
                return new ArtistReview(rs.getLong("id"), artist, user, title, description, rating, createdAt, likes, isBlocked);
            } else {
                return null;
            }
        }

        public List<Review> mapRows(ResultSet rs) throws SQLException {
            List<Review> reviews = new ArrayList<>();
            while (rs.next()) {
                Review r = mapRow(rs, rs.getRow());
                if (r != null) reviews.add(r);
            }
            return reviews;
        }

        private Long getLongOrNull(ResultSet rs, String columnName) throws SQLException {
            long value = rs.getLong(columnName);
            return rs.wasNull() ? null : value;
        }
    }

    private final ReviewRowMapper reviewRowMapper = new ReviewRowMapper();

    public ReviewJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Optional<Review> find(long id) {
        return Optional.empty();
    }

    @Override
    public List<Review> findAll() {
        return null;
    }

    @Override
    public List<Review> findPaginated(FilterType filterType, int limit, int offset) {
        return null;
    }

    @Override
    public Review create(Review entity) {
        return null;
    }

    @Override
    public Review update(Review review) {
        int result = jdbcTemplate.update(
                "UPDATE review SET title = ?, description = ?, rating = ?, likes = ?, isblocked = ? WHERE id = ?",
                review.getTitle(),
                review.getDescription(),
                review.getRating(),
                review.getLikes(),
                review.isBlocked(),
                review.getId()
        );
        if (result == 1)
            return review;
        else
            throw new IllegalStateException("Failed to update review");
    }

    @Override
    public boolean delete(long id) {
        return jdbcTemplate.update("DELETE FROM review WHERE id = ?", id) == 1;
    }


    @Override
    public Optional<ArtistReview> findArtistReviewById(long id) {
        return jdbcTemplate.query(
                "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, u.verified, u.moderator, a.id AS artist_id, a.name AS artist_name, a.img_id AS artist_img_id " +
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
    public Optional<ArtistReview> findArtistReviewByUserId(long userId, long artistId) {
        return jdbcTemplate.query(
                "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, u.verified, u.moderator, a.id AS artist_id, a.name AS artist_name, a.img_id AS artist_img_id " +
                        "FROM review r " +
                        "JOIN cuser u ON r.user_id = u.id " +
                        "JOIN artist_review ar ON r.id = ar.review_id " +
                        "JOIN artist a ON ar.artist_id = a.id " +
                        "WHERE ar.artist_id = ? AND r.user_id = ?",
                new Object[]{artistId, userId},
                new int[]{Types.BIGINT, Types.BIGINT},
                ARTIST_REVIEW_ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<ArtistReview> findReviewsByArtistId(long artistId) {
        return jdbcTemplate.query(
                "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, u.verified, u.moderator, a.id AS artist_id, a.name AS artist_name, a.img_id AS artist_img_id " +
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
    public ArtistReview saveArtistReview(ArtistReview review) {
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
        return review;
    }



    @Override
    public Optional<AlbumReview> findAlbumReviewById(long id) {
        return jdbcTemplate.query(
                "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, u.verified, u.moderator, al.id AS album_id, al.title AS album_title, al.img_id AS album_img_id, al.artist_id AS album_artist_id, al.release_date AS album_release_date, al.genre " +
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
    public Optional<AlbumReview> findAlbumReviewByUserId(long userId, long albumId) {
        return jdbcTemplate.query(
                "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, u.verified, u.moderator, al.id AS album_id, al.title AS album_title, al.img_id AS album_img_id, al.artist_id AS album_artist_id, al.release_date AS album_release_date, al.genre " +
                        "FROM review r " +
                        "JOIN cuser u ON r.user_id = u.id " +
                        "JOIN album_review ar ON r.id = ar.review_id " +
                        "JOIN album al ON ar.album_id = al.id " +
                        "WHERE ar.album_id = ? AND r.user_id = ?",
                new Object[]{albumId, userId},
                new int[]{Types.BIGINT, Types.BIGINT},
                ALBUM_REVIEW_ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<AlbumReview> findReviewsByAlbumId(long albumId) {
        return jdbcTemplate.query(
                "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, u.verified, u.moderator, al.id AS album_id, al.title AS album_title, al.img_id AS album_img_id, al.artist_id AS album_artist_id, al.release_date AS album_release_date, al.genre " +
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
    public AlbumReview saveAlbumReview(AlbumReview review) {
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
        return review;
    }


    @Override
    public Optional<SongReview> findSongReviewById(long id) {
        return jdbcTemplate.query(
                "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, u.verified, u.moderator, " +
                        "s.id AS song_id, s.title AS song_title, s.duration, " +
                        "al.id AS album_id, al.title AS album_title, al.img_id AS album_img_id, al.artist_id AS album_artist_id, al.release_date AS album_release_date, al.genre " +
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
                "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, u.verified, u.moderator, " +
                        "s.id AS song_id, s.title AS song_title, s.duration, " +
                        "al.id AS album_id, al.title AS album_title, al.img_id AS album_img_id, al.artist_id AS album_artist_id, al.release_date AS album_release_date, al.genre " +
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
    public Optional<SongReview> findSongReviewByUserId(long userId, long songId) {
        return jdbcTemplate.query(
                "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, u.verified, u.moderator, " +
                        "s.id AS song_id, s.title AS song_title, s.duration, " +
                        "al.id AS album_id, al.title AS album_title, al.img_id AS album_img_id, al.artist_id AS album_artist_id, al.release_date AS album_release_date, al.genre " +
                        "FROM review r " +
                        "JOIN cuser u ON r.user_id = u.id " +
                        "JOIN song_review sr ON r.id = sr.review_id " +
                        "JOIN song s ON sr.song_id = s.id " +
                        "JOIN album al ON s.album_id = al.id " +
                        "WHERE sr.song_id = ? AND r.user_id = ?",
                new Object[]{songId, userId},
                new int[]{Types.BIGINT, Types.BIGINT},
                SONG_REVIEW_ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public SongReview saveSongReview(SongReview review) {
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
        return review;
    }

    @Override
    public List<SongReview> findSongReviewsPaginated(long songId, int page, int pageSize) {
        return jdbcTemplate.query(
                "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, u.verified, u.moderator, " +
                        "s.id AS song_id, s.title AS song_title, s.duration, " +
                        "al.id AS album_id, al.title AS album_title, al.img_id AS album_img_id, al.artist_id AS album_artist_id, al.release_date AS album_release_date, al.genre " +
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
    public void createLike(long userId, long reviewId) {
        jdbcTemplate.update("INSERT INTO review_like (user_id, review_id) VALUES (?, ?)", userId, reviewId);
    }

    @Override
    public void deleteLike(long userId, long reviewId) {
        jdbcTemplate.update("DELETE FROM review_like WHERE user_id = ? AND review_id = ?", userId, reviewId);
    }

    @Override
    public void incrementLikes(long reviewId) {
        jdbcTemplate.update("UPDATE review SET likes = likes + 1 WHERE id = ?", reviewId);
    }

    @Override
    public void decrementLikes(long reviewId) {
        jdbcTemplate.update("UPDATE review SET likes = likes - 1 WHERE id = ?", reviewId);
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
                "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, u.verified, u.moderator, a.id AS artist_id, a.name AS artist_name, a.img_id AS artist_img_id " +
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
                "SELECT r.*, u.username, u.name AS user_name, u.img_id AS user_img_id, u.verified, u.moderator, al.id AS album_id, al.title AS album_title, al.img_id AS album_img_id, al.artist_id AS album_artist_id, al.release_date AS album_release_date, al.genre " +
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
    public List<Review> getPopularReviewsPaginated(int page, int pageSize) {
        String sql = "SELECT r.id, r.title, r.description, r.rating, r.created_at, r.likes, r.isblocked AS isBlocked, u.id AS user_id, u.username, u.name AS user_name, u.img_id AS user_img_id, u.verified, u.moderator, sr.review_id AS song_review_id, s.id AS song_id, s.title AS song_title, s.duration, alr.review_id AS album_review_id, a.id AS album_id, a.title AS album_title, a.img_id AS album_img_id, a.genre, a.release_date AS album_release_date, arr.review_id AS artist_review_id, ar.id AS artist_id, ar.name AS artist_name, ar.img_id AS artist_img_id, aa.id AS album_artist_id " +
                "FROM review r "+
                "LEFT JOIN cuser u ON r.user_id = u.id "+
                "LEFT JOIN song_review sr ON r.id = sr.review_id "+
                "LEFT JOIN song s ON sr.song_id = s.id "+
                "LEFT JOIN album_review alr ON r.id = alr.review_id "+
                "LEFT JOIN album a ON alr.album_id = a.id OR a.id = s.album_id "+
                "LEFT JOIN artist_review arr ON r.id = arr.review_id "+
                "LEFT JOIN artist ar ON arr.artist_id = ar.id OR a.artist_id = ar.id "+
                "LEFT JOIN artist aa ON a.artist_id = aa.id " +
                "WHERE r.isblocked = false " +
                "ORDER BY r.likes DESC " +
                "LIMIT ? OFFSET ?";

        int offset = (page - 1) * pageSize;
        return jdbcTemplate.query(sql, new Object[]{pageSize, offset}, reviewRowMapper::mapRows);
    }

    @Override
    public List<Review> getReviewsFromFollowedUsersPaginated(Long userId, int page, int pageSize) {
        String sql = "SELECT r.id, r.title, r.description, r.rating, r.created_at, r.likes, r.isblocked AS isBlocked, u.id AS user_id, u.username, u.name AS user_name, u.img_id AS user_img_id, u.verified, u.moderator, sr.review_id AS song_review_id, s.id AS song_id, s.title AS song_title, s.duration, alr.review_id AS album_review_id, a.id AS album_id, a.title AS album_title, a.img_id AS album_img_id, a.genre, a.release_date AS album_release_date, arr.review_id AS artist_review_id, ar.id AS artist_id, ar.name AS artist_name, ar.img_id AS artist_img_id, aa.id AS album_artist_id " +
                "FROM review r "+
                "JOIN follower f ON r.user_id = f.following " +
                "LEFT JOIN cuser u ON r.user_id = u.id "+
                "LEFT JOIN song_review sr ON r.id = sr.review_id "+
                "LEFT JOIN song s ON sr.song_id = s.id "+
                "LEFT JOIN album_review alr ON r.id = alr.review_id "+
                "LEFT JOIN album a ON alr.album_id = a.id OR a.id = s.album_id "+
                "LEFT JOIN artist_review arr ON r.id = arr.review_id "+
                "LEFT JOIN artist ar ON arr.artist_id = ar.id OR a.artist_id = ar.id "+
                "LEFT JOIN artist aa ON a.artist_id = aa.id " +
                "WHERE f.user_id = ? AND r.isblocked = false " +
                "ORDER BY r.created_at DESC " +
                "LIMIT ? OFFSET ?";

        int offset = (page - 1) * pageSize;
        return jdbcTemplate.query(sql, new Object[]{userId, pageSize, offset}, reviewRowMapper::mapRows);
    }

    @Override
    public List<Review> findReviewsByUserPaginated(Long userId, int page, int pageSize) {
        String sql = "SELECT r.id, r.title, r.description, r.rating, r.created_at, r.likes, r.isblocked AS isBlocked, u.id AS user_id, u.username, u.name AS user_name, u.img_id AS user_img_id, u.verified, u.moderator, sr.review_id AS song_review_id, s.id AS song_id, s.title AS song_title, s.duration, alr.review_id AS album_review_id, a.id AS album_id, a.title AS album_title, a.img_id AS album_img_id, a.genre, a.release_date AS album_release_date, arr.review_id AS artist_review_id, ar.id AS artist_id, ar.name AS artist_name, ar.img_id AS artist_img_id, aa.id AS album_artist_id " +
                "FROM review r "+
                "LEFT JOIN cuser u ON r.user_id = u.id "+
                "LEFT JOIN song_review sr ON r.id = sr.review_id "+
                "LEFT JOIN song s ON sr.song_id = s.id "+
                "LEFT JOIN album_review alr ON r.id = alr.review_id "+
                "LEFT JOIN album a ON alr.album_id = a.id OR a.id = s.album_id "+
                "LEFT JOIN artist_review arr ON r.id = arr.review_id "+
                "LEFT JOIN artist ar ON arr.artist_id = ar.id OR a.artist_id = ar.id "+
                "LEFT JOIN artist aa ON a.artist_id = aa.id " +
                "WHERE r.user_id = ? " +
                "ORDER BY r.created_at DESC " +
                "LIMIT ? OFFSET ?";

        int offset = (page - 1) * pageSize;
        return jdbcTemplate.query(sql, new Object[]{userId, pageSize, offset}, reviewRowMapper::mapRows);
    }

    @Override
    public void block(Long reviewId) {
        jdbcTemplate.update("UPDATE review SET isBlocked = true WHERE id = ?", reviewId);
    }

    @Override
    public void unblock(Long reviewId) {
        jdbcTemplate.update("UPDATE review SET isBlocked = false WHERE id = ?", reviewId);
    }

}