package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
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
    public int create(User user) {
        return jdbcTemplate.update(
                "INSERT INTO cuser (username, email, password, name, bio, created_at, updated_at, verified, img_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.getBio(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.isVerified(),
                user.getImgId()
        );
    }

    @Override
    public int createFollowing(User user, User following) {
        updateFollowingAmount(user, user.getFollowingAmount() + 1);
        updateFollowersAmount(following, following.getFollowersAmount() + 1);
        return jdbcTemplate.update(
                "INSERT INTO follower (user_id, following) VALUES (?, ?)",
                user.getId(),
                following.getId()
        );
    }

    @Override
    public int undoFollowing(User user, User following) {
        updateFollowingAmount(user, user.getFollowingAmount() - 1);
        updateFollowersAmount(following, following.getFollowersAmount() - 1);
        return jdbcTemplate.update(
                "DELETE FROM follower WHERE user_id = ? AND following = ?",
                user.getId(),
                following.getId()
        );
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
    public int update(User user) {
        return jdbcTemplate.update(
                "UPDATE cuser SET username = ?, email = ?, password = ?, name = ?, bio = ?, updated_at = ?, verified = ?, img_id = ?, followers_amount = ?, following_amount = ?, review_amount = ? WHERE id = ?",
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.getBio(),
                user.getUpdatedAt(),
                user.isVerified(),
                user.getImgId(),
                user.getFollowersAmount(),
                user.getFollowingAmount(),
                user.getReviewAmount(),
                user.getId()
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


}
