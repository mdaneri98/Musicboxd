package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.Artist;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "classpath:artist_setUp.sql")
public class ArtistJdbcDaoTest {

    private static final long PRE_EXISTING_ARTISTID = 500;
    private static final String PRE_EXISTING_ARTISTNAME = "Dummy";
    private static final long PRE_EXISTING_IMAGEID = 1;

    private static final long NEW_ARTISTID = 1000;
    private static final String NEW_ARTISTNAME = "DummyX";


    @Autowired
    private ArtistJdbcDao artistDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testFindById_ExistingArtist() throws SQLException {
        // 1. Pre-condiciones - existe un artista con id PRE_EXISTING_ARTISTID

        // 2. Ejercitar
        Optional<Artist> maybeArtist = artistDao.findById(PRE_EXISTING_ARTISTID);

        // 3. Post-condiciones
        assertTrue(maybeArtist.isPresent());
        assertEquals(PRE_EXISTING_ARTISTID, maybeArtist.get().getId().longValue());
    }

    @Test(expected = NoSuchElementException.class)
    public void testFindById_NonExistingArtist() throws SQLException {
        // 1. Pre-condiciones - no existe el artista

        // 2. Ejercitar
        Optional<Artist> maybeArtist = artistDao.findById(NEW_ARTISTID);

        // 3. Post-condiciones
        assertFalse(maybeArtist.isPresent());
        assertEquals(NEW_ARTISTID, maybeArtist.get().getId().longValue());
    }

    @Test
    public void testFindAll() throws SQLException {
        // 1. Pre-condiciones - existen varios artistas

        // 2. Ejercitar
        artistDao.findAll();

        // 3. Post-condiciones
        assertEquals(5, JdbcTestUtils.countRowsInTable(jdbcTemplate,"artist"));
    }

    @Test
    public void testSave() throws SQLException {
        // 1. Pre-condiciones - no existe el artist
        Artist artist = new Artist(null,NEW_ARTISTNAME,null,null,null,PRE_EXISTING_IMAGEID);

        // 2. Ejercitar
        int rowsAffected = artistDao.save(artist);

        // 3. Post-condiciones
        assertEquals(1, rowsAffected);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"artist", String.format("name = '%s'", NEW_ARTISTNAME)));
    }

    @Test
    public void testUpdate_ExistingArtist() throws SQLException {
        // 1. Pre-condiciones - existe el artist
        Artist artist = new Artist(PRE_EXISTING_ARTISTID, NEW_ARTISTNAME,null,null,null,PRE_EXISTING_IMAGEID);

        // 2. Ejercitar
        int rowsAffected = artistDao.update(artist);

        // 3. Post-condiciones
        assertEquals(1, rowsAffected);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"artist",
                String.format("id = %d AND name = '%s'", PRE_EXISTING_ARTISTID, NEW_ARTISTNAME)));
    }

    @Test
    public void testUpdate_NonExistingArtist() throws SQLException {
        // 1. Pre-condiciones - existe el artist
        Artist artist = new Artist(null, NEW_ARTISTNAME,null,null,null,PRE_EXISTING_IMAGEID);

        // 2. Ejercitar
        int rowsAffected = artistDao.update(artist);

        // 3. Post-condiciones
        assertEquals(0, rowsAffected);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"artist",
                String.format("name = '%s'", NEW_ARTISTNAME)));
    }

    @Test
    public void testDeleteById_ExistingArtist() throws SQLException {
        // 1. Pre-condiciones - existe el artist

        // 2. Ejercitar
        int rowsAffected = artistDao.deleteById(PRE_EXISTING_ARTISTID);

        // 3. Post-condiciones
        assertEquals(1, rowsAffected);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"artist", String.format("id = %d", PRE_EXISTING_ARTISTID)));
    }

    @Test
    public void testDeleteById_NonExistingArtist() throws SQLException {
        // 1. Pre-condiciones - existe el artist

        // 2. Ejercitar
        int rowsAffected = artistDao.deleteById(NEW_ARTISTID);

        // 3. Post-condiciones
        assertEquals(0, rowsAffected);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"artist", String.format("id = %d", NEW_ARTISTID)));
    }
}
