package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserJdbcDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;


    public UserJdbcDao(final DataSource ds) {
        //El JdbcTemplate le quita complejidad al DataSource hecho por Java.
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Optional<User> findById(long id) {
        // Jamás concatener valores en una query("SELECT ... WHERE username = " + id).
        return jdbcTemplate.query("SELECT * FROM cuser WHERE id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                SimpleRowMappers.USER_ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM cuser", SimpleRowMappers.USER_ROW_MAPPER);
    }

    @Override
    public List<User> findByUsernameContaining(String sub) {
        String sql = "SELECT * FROM cuser WHERE username ILIKE ?";

        // Ejecutamos la consulta y mapeamos los resultados a objetos User
        return jdbcTemplate.query(sql, new Object[]{"%" + sub + "%"}, SimpleRowMappers.USER_ROW_MAPPER);
    }

    @Override
    public int create(String username, String email, String password) {
        int imgId = 1;

        String checkImageSql = "SELECT COUNT(*) FROM image WHERE id = ?";
        int count = jdbcTemplate.queryForObject(checkImageSql, Integer.class, imgId);

        if (count == 0) {
            // Image doesn't exist, throw an exception.
            throw new IllegalArgumentException("Image with id " + imgId + " does not exist");
        }

        // If we get here, the image exists, so we can proceed with user creation
        String insertUserSql = "INSERT INTO cuser (username, email, password, img_id) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(insertUserSql, username, email, password, imgId);
    }

    @Override
    public boolean isFollowing(Long userId, Long otherId) {
        String sql = "SELECT COUNT(*) FROM follower WHERE user_id = ? AND following = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, userId, otherId);
        return count > 0;
    }

    @Override
    public boolean isArtistFavorite(Long userId, Long artistId) {
        String sql = "SELECT COUNT(*) FROM favorite_artist WHERE user_id = ? AND artist_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, userId, artistId);
        return count > 0;
    }

    @Override
    public boolean isAlbumFavorite(Long userId, Long albumId) {
        String sql = "SELECT COUNT(*) FROM favorite_album WHERE user_id = ? AND album_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, userId, albumId);
        return count > 0;
    }

    @Override
    public boolean isSongFavorite(Long userId, Long songId) {
        String sql = "SELECT COUNT(*) FROM favorite_song WHERE user_id = ? AND song_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, userId, songId);
        return count > 0;
    }

    @Override
    public List<User> getFollowers(Long userId, int limit, int offset) {
        String sql = "SELECT user_id FROM follower WHERE following = ? LIMIT ? OFFSET ?";
        List<Long> followerIds = jdbcTemplate.queryForList(sql, new Object[]{userId, limit, offset}, Long.class);

        List<User> followers = new ArrayList<>();
        for (Long followerId : followerIds) {
            Optional<User> follower = this.findById(followerId);
            follower.ifPresent(followers::add);
        }
        return followers;
    }

    @Override
    public List<User> getFollowing(Long userId, int limit, int offset) {
        String sql = "SELECT following FROM follower WHERE user_id = ? LIMIT ? OFFSET ?";
        List<Long> followingIds = jdbcTemplate.queryForList(sql, new Object[]{userId, limit, offset}, Long.class);

        List<User> following = new ArrayList<>();
        for (Long followingId : followingIds) {
            Optional<User> follower = this.findById(followingId);
            follower.ifPresent(following::add);
        }
        return following;
    }

    @Override
    public int createFollowing(User user, User following) {
        int result = jdbcTemplate.update(
                "INSERT INTO follower (user_id, following) VALUES (?, ?)",
                user.getId(),
                following.getId()
        );
        if (result == 1) {
            updateFollowingAmount(user, user.getFollowingAmount() + 1);
            updateFollowersAmount(following, following.getFollowersAmount() + 1);
        }
        return result;
    }

    @Override
    public int undoFollowing(User user, User following) {
        int result = jdbcTemplate.update(
                "DELETE FROM follower WHERE user_id = ? AND following = ?",
                user.getId(),
                following.getId()
        );
        if (result == 1) {
            int followingAmount = user.getFollowingAmount();
            int followersAmount = following.getFollowersAmount();
            updateFollowingAmount(user, followingAmount == 0 ? 0 : followingAmount - 1);
            updateFollowersAmount(following,  followersAmount == 0 ? 0 : followersAmount - 1);
        }
        return result;
    }

    private int updateFollowingAmount(User user, int amount) {
        user.setFollowingAmount(amount);
        return jdbcTemplate.update("UPDATE cuser SET following_amount = ?  WHERE id = ?", amount, user.getId());
    }

    private int updateFollowersAmount(User user, int amount) {
        user.setFollowersAmount(amount);
        return jdbcTemplate.update("UPDATE cuser SET followers_amount = ?  WHERE id = ?", amount, user.getId());
    }

    @Override
    public int update(Long userId, String username, String email, String password, String name, String bio, LocalDateTime updated_at, boolean verified, boolean moderator, Long imgId, Integer followers_amount, Integer following_amount, Integer review_amount) {
        return jdbcTemplate.update(
                "UPDATE cuser SET username = ?, email = ?, password = ?, name = ?, bio = ?, updated_at = ?, verified = ?, moderator = ?, img_id = ?, followers_amount = ?, following_amount = ?, review_amount = ? WHERE id = ?",
                username,
                email,
                password,
                name,
                bio,
                updated_at,
                verified,
                moderator,
                imgId,
                followers_amount,
                following_amount,
                review_amount,
                userId
        );
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jdbcTemplate.query("SELECT * FROM cuser WHERE email = ?",
                new Object[]{ email },
                new int[]{Types.VARCHAR},
                SimpleRowMappers.USER_ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jdbcTemplate.query("SELECT * FROM cuser WHERE username = ?",
                new Object[]{ username },
                new int[]{Types.VARCHAR},
                SimpleRowMappers.USER_ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM cuser WHERE id = ?", id);
    }



    @Override
    public List<Artist> getFavoriteArtists(long userId) {
        final String sql = "SELECT a.* FROM artist a " +
                "JOIN favorite_artist fa ON a.id = fa.artist_id " +
                "WHERE fa.user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, SimpleRowMappers.ARTIST_ROW_MAPPER);
    }

    @Override
    public boolean addFavoriteArtist(long userId, long artistId) {
        int currentCount = getFavoriteArtistsCount(userId);
        if (currentCount >= 5) {
            return false;
        }

        final String sql = "INSERT INTO favorite_artist (user_id, artist_id) VALUES (?, ?)";

        try {
            jdbcTemplate.update(sql, userId, artistId);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    @Override
    public boolean removeFavoriteArtist(long userId, long artistId) {
        final String sql = "DELETE FROM favorite_artist WHERE user_id = ? AND artist_id = ?";
        return jdbcTemplate.update(sql, userId, artistId) > 0;
    }

    @Override
    public int getFavoriteArtistsCount(long userId) {
        final String sql = "SELECT COUNT(*) FROM favorite_artist WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{userId}, Integer.class);
    }

    @Override
    public List<Album> getFavoriteAlbums(long userId) {
        final String sql = "SELECT a.id AS album_id, title, genre, release_date, a.created_at, a.updated_at, a.img_id AS album_img_id, a.avg_rating AS avg_rating, a.rating_amount AS rating_amount, ar.id AS artist_id, name, ar.img_id AS artist_img_id FROM album a " +
                "JOIN favorite_album fa ON a.id = fa.album_id JOIN artist ar ON a.artist_id = ar.id " +
                "WHERE fa.user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, SimpleRowMappers.ALBUM_ROW_MAPPER);
    }

    @Override
    public boolean addFavoriteAlbum(long userId, long albumId) {
        int currentCount = getFavoriteAlbumsCount(userId);
        if (currentCount >= 5) {
            return false;
        }

        final String sql = "INSERT INTO favorite_album (user_id, album_id) VALUES (?, ?)";

        try {
            jdbcTemplate.update(sql, userId, albumId);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    @Override
    public boolean removeFavoriteAlbum(long userId, long albumId) {
        final String sql = "DELETE FROM favorite_album WHERE user_id = ? AND album_id = ?";
        return jdbcTemplate.update(sql, userId, albumId) > 0;
    }

    @Override
    public int getFavoriteAlbumsCount(long userId) {
        final String sql = "SELECT COUNT(*) FROM favorite_album WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{userId}, Integer.class);
    }

    @Override
    public List<Song> getFavoriteSongs(long userId) {
        final String sql = "SELECT s.id AS song_id, s.title AS song_title, duration, track_number, s.created_at AS song_created_at, s.updated_at AS song_updated_at, s.avg_rating AS avg_rating, s.rating_amount AS rating_amount, al.id AS album_id, al.title AS album_title, al.img_id AS album_img_id, al.genre, ar.id AS artist_id, name, ar.img_id AS artist_img_id, al.release_date AS album_release_date FROM song s " +
                "JOIN favorite_song fs ON s.id = fs.song_id JOIN album al ON s.album_id = al.id JOIN artist ar ON al.artist_id = ar.id " +
                "WHERE fs.user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, SimpleRowMappers.SONG_ROW_MAPPER);
    }

    @Override
    public boolean addFavoriteSong(long userId, long songId) {
        int currentCount = getFavoriteSongsCount(userId);
        if (currentCount >= 5) {
            return false;
        }

        final String sql = "INSERT INTO favorite_song (user_id, song_id) VALUES (?, ?)";

        try {
            jdbcTemplate.update(sql, userId, songId);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    @Override
    public boolean removeFavoriteSong(long userId, long songId) {
        final String sql = "DELETE FROM favorite_song WHERE user_id = ? AND song_id = ?";
        return jdbcTemplate.update(sql, userId, songId) > 0;
    }

    @Override
    public int getFavoriteSongsCount(long userId) {
        final String sql = "SELECT COUNT(*) FROM favorite_song WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{userId}, Integer.class);
    }
}
