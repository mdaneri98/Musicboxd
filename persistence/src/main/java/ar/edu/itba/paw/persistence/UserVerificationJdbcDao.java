package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserVerification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;

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
    public void startVerification(User user, String code) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireDateTime = now.plusHours(6);

        jdbcTemplate.update(
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
                return userDao.update(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(), user.getName(), user.getBio(), user.getUpdatedAt(), true, user.isModerator(), user.getImgId(), user.getFollowersAmount(), user.getFollowingAmount(), user.getReviewAmount()) == 1;
            }
        }
        return false;
    }
}
