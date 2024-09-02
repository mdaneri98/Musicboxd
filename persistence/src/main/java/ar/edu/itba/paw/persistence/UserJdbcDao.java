package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDateTime;
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
    public int save(User user) {
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
    public int update(User user) {
        return jdbcTemplate.update(
                "UPDATE cuser SET username = ?, email = ?, password = ?, name = ?, bio = ?, updated_at = ?, verified = ?, img_id = ? WHERE id = ?",
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.getBio(),
                user.getUpdatedAt(),
                user.isVerified(),
                user.getImgId(),
                user.getId()
        );
    }

    @Override
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM cuser WHERE id = ?", id);
    }

}
