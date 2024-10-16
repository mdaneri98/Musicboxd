package ar.edu.itba.paw.persistence.jdbc;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserVerification;
import ar.edu.itba.paw.models.VerificationType;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.persistence.UserVerificationDao;
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
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.userDao = userDao;
    }

    @Override
    public void startVerification(VerificationType type, User user, String code) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireDateTime = now.plusHours(6);

        jdbcTemplate.update(
                "INSERT INTO verification (user_id, code, expire_date, vtype) VALUES (?, ?, ?, ?)",
                user.getId(),
                code,
                expireDateTime,
                type.toString()
        );
    }

    @Override
    public Long verify(VerificationType type, String code) {
        List<UserVerification> verifications = jdbcTemplate.query("SELECT * FROM verification WHERE code = ? AND vtype = ?",
                new Object[]{ code, type },
                new int[]{Types.VARCHAR, Types.VARCHAR},
                SimpleRowMappers.USER_VERIFICATION_ROW_MAPPER
                );
        if (!verifications.isEmpty()) {
            UserVerification userVerification = verifications.getFirst();

            User user = userDao.find(userVerification.getUser_id()).orElseThrow();
            if (userVerification.getExpireDate().after(Timestamp.valueOf(LocalDateTime.now()))) {
                user.setVerified(true);
                if (userDao.update(user) == 1)
                    return user.getId();
            }
        }
        return 0L;
    }
}
