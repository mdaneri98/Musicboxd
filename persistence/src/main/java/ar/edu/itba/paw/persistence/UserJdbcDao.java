package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class UserJdbcDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    /*
    * Se puede hacer ya que es STATELESS, sin problemas de concurrencia.
    * Todas las queries van a mappear cada row exactamente igual.
    * */
    private static final RowMapper<User> ROW_MAPPER = (rs, rowNum) -> new User(
            rs.getLong("id"),
            rs.getString("username"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("name"),
            rs.getString("bio"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getObject("updated_at", LocalDateTime.class)
    );

    public UserJdbcDao(final DataSource ds) {
        //El JdbcTemplate le quita complejidad al DataSource hecho por Java.
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Optional<User> findById(long id) {
        // Jamás concatener valores en una query("SELECT ... WHERE username = " + id).
        return jdbcTemplate.query("SELECT * FROM users WHERE id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                ROW_MAPPER
        ).stream().findFirst();
    }

}
/*
new RowMapper<User>() {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User(rs.getLong("userId"), rs.getString("username"));
    }
}
*/
