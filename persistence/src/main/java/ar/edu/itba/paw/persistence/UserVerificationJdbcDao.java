package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserVerification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class UserVerificationJdbcDao implements UserVerificationDao {

    private final JdbcTemplate jdbcTemplate;

    private final UserDao userDao;

    public UserVerificationJdbcDao(final DataSource ds, final UserDao userDao) {
        //El JdbcTemplate le quita complejidad al DataSource hecho por Java.
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.userDao = userDao;
    }

    @Override
    public int startVerification(User user, String code) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireDateTime = now.plusHours(6);

        return jdbcTemplate.update(
                "INSERT INTO verify_user (user_id, code, expire_date) VALUES (?, ?, ?)",
                user.getId(),
                code,
                expireDateTime
        );
    }

    @Override
    public boolean verify(String code) {
        List<UserVerification> verifications = jdbcTemplate.query("SELECT * FROM verify_user WHERE code = ?",
                new Object[]{ code },
                new int[]{Types.VARCHAR},
                SimpleRowMappers.USER_VERIFICATION_ROW_MAPPER
                );
        if (!verifications.isEmpty()) {
            UserVerification userVerification = verifications.getFirst();

            User user = userDao.findById(userVerification.getUser_id()).orElseThrow();
            if (userVerification.getExpireDate().after(Timestamp.valueOf(LocalDateTime.now()))) {
                user.setVerified(true);
                return userDao.update(user) == 1;
            }
        }
        return false;
    }
}
