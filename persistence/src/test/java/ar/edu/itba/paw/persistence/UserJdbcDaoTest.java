package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.User;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.*;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "classpath:cuser_setUp.sql")
public class UserJdbcDaoTest {

    private static final long PRE_EXISTING_USERID = 500;
    private static final String PRE_EXISTING_USERNAME = "Dummy";
    private static final String PRE_EXISTING_EMAIL = "dummy@example.com";

    private static final long NON_EXISTING_USERID = 1001;
    private static final String USERNAME = "DummyX";
    private static final String EMAIL = "dummyX@example.com";
    private static final String PASSWORD = "dummyX123";


    @Autowired
    private UserJdbcDao userDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testFindById_ExistingUser() throws SQLException {
        // 1. Pre-condiciones - existe un usuario con id PRE_EXISTING_USERID

        // 2. Ejercitar
        Optional<User> maybeUser = userDao.findById(PRE_EXISTING_USERID);

        // 3. Post-condiciones
        assertTrue(maybeUser.isPresent());
        assertEquals(PRE_EXISTING_USERID, maybeUser.get().getId().longValue());
    }

    @Test(expected = NoSuchElementException.class)
    public void testFindById_NonExistingUser() throws SQLException {
        // 1. Pre-condiciones - no existe el usuario

        // 2. Ejercitar
        Optional<User> maybeUser = userDao.findById(NON_EXISTING_USERID);

        // 3. Post-condiciones
        assertFalse(maybeUser.isPresent());
        assertEquals(NON_EXISTING_USERID, maybeUser.get().getId().longValue());
    }

    @Test
    public void testFindAll() throws SQLException {
        // 1. Pre-condiciones - existen varios usuarios

        // 2. Ejercitar
        userDao.findAll();

        // 3. Post-condiciones
        assertEquals(5, JdbcTestUtils.countRowsInTable(jdbcTemplate,"cuser"));
    }

    @Test
    public void testSave_NonExistingUser() throws SQLException {
        // 1. Pre-condiciones - no existe el user
        User user = new User(null,USERNAME,EMAIL,
                PASSWORD,null,null,null,null,null);

        // 2. Ejercitar
        int rowsAffected = userDao.save(user);

        // 3. Post-condiciones
        assertEquals(1, rowsAffected);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"cuser", String.format("username = '%s'", USERNAME)));
    }

    @Test(expected = DuplicateKeyException.class)
    public void testSave_ExistingUser() throws SQLException {
        // 1. Pre-condiciones - no existe el user
        User user = new User(null, PRE_EXISTING_USERNAME, PRE_EXISTING_EMAIL,
                PASSWORD,null,null,null,null,null);

        // 2. Ejercitar
        int rowsAffected = userDao.save(user);

        // 3. Post-condiciones
        fail();
    }

    @Test
    public void testUpdate_ExistingUser() throws SQLException {
        // 1. Pre-condiciones - existe el user
        User user = new User(PRE_EXISTING_USERID, USERNAME, EMAIL,
                PASSWORD,null,null,null,null,null);

        // 2. Ejercitar
        int rowsAffected = userDao.update(user);

        // 3. Post-condiciones
        assertEquals(1, rowsAffected);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"cuser",
                String.format("id = %d AND username = '%s'", PRE_EXISTING_USERID, USERNAME)));
    }

    @Test
    public void testUpdate_NonExistingUser() throws SQLException {
        // 1. Pre-condiciones - existe el user
        User user = new User(null, USERNAME, EMAIL,
                PASSWORD,null,null,null,null,null);

        // 2. Ejercitar
        int rowsAffected = userDao.update(user);

        // 3. Post-condiciones
        assertEquals(0, rowsAffected);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"cuser",
                String.format("username = '%s'", USERNAME)));
    }

    @Test
    public void testDeleteById_ExistingUser() throws SQLException {
        // 1. Pre-condiciones - existe el user

        // 2. Ejercitar
        int rowsAffected = userDao.deleteById(PRE_EXISTING_USERID);

        // 3. Post-condiciones
        assertEquals(1, rowsAffected);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"cuser", String.format("id = %d", PRE_EXISTING_USERID)));
    }

    @Test
    public void testDeleteById_NonExistingUser() throws SQLException {
        // 1. Pre-condiciones - existe el user

        // 2. Ejercitar
        int rowsAffected = userDao.deleteById(NON_EXISTING_USERID);

        // 3. Post-condiciones
        assertEquals(0, rowsAffected);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"cuser", String.format("id = %d", NON_EXISTING_USERID)));
    }
}
