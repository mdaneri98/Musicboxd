package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
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
import java.util.List;
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

    private static final long PRE_EXISTING_IMAGE_ID = 100;
    private static final long PRE_EXISTING_USER_ID = 200;
    private static final long PRE_EXISTING_ARTIST_ID = 300;
    private static final long PRE_EXISTING_ARTIST_2_ID = 301;
    private static final long PRE_EXISTING_REVIEW_ID = 400;
    private static final long PRE_EXISTING_ALBUM_ID = 500;
    private static final long PRE_EXISTING_SONG_ID = 600;

    private static final long NEW_ARTIST_ID = 1000;
    private static final long NEW_SONG_ID = 1000;

    private static final String PRE_EXISTING_ARTIST_NAME = "Dummy";
    private static final String NEW_ARTIST_NAME = "DummyX";
    private static final String NEW_ARTIST_BIO = "This is a description of DummyX";
    private static final float NEW_ARTIST_AVG_RATING = 3.4F;
    private static final int NEW_ARTIST_RATING_AMOUNT = 15;



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
    public void test_find() {
        // 1. Pre-conditions - the artist exist

        // 2. Execute
        Optional<Artist> maybeArtist = artistDao.find(PRE_EXISTING_ARTIST_ID);

        // 3. Post-conditions
        assertTrue(maybeArtist.isPresent());
        assertEquals(PRE_EXISTING_ARTIST_ID, maybeArtist.get().getId().longValue());
        assertEquals(PRE_EXISTING_ARTIST_NAME, maybeArtist.get().getName());
        assertEquals(PRE_EXISTING_IMAGE_ID, maybeArtist.get().getImgId().longValue());
    }

    @Test(expected = NoSuchElementException.class)
    public void test_find_NoArtist() {
        // 1. Pre-conditions - the artist does not exist

        // 2. Execute
        Optional<Artist> maybeArtist = artistDao.find(NEW_ARTIST_ID);

        // 3. Post-conditions
        assertFalse(maybeArtist.isPresent());
        assertEquals(NEW_ARTIST_ID, maybeArtist.get().getId().longValue());
    }

    @Test
    public void test_findAll() {
        // 1. Pre-conditions - Only 5 artists exist in database

        // 2. Execute
        List<Artist> artistList = artistDao.findAll();

        // 3. Post-conditions
        assertEquals(5, artistList.size());
    }

    @Test
    public void test_findPaginated() {
        // 1. Pre-conditions - 5 artists exist in database

        // 2. Execute
        List<Artist> artistList = artistDao.findPaginated(FilterType.RATING, 3,1);

        // 3. Post-conditions
        assertEquals(3, artistList.size()); //Correct limit
        assertEquals(PRE_EXISTING_ARTIST_2_ID, artistList.getFirst().getId().longValue());  //Correct offset
    }

    @Test
    public void test_findPaginated_endPage() {
        // 1. Pre-conditions - 5 artists exist in database

        // 2. Execute
        List<Artist> artistList = artistDao.findPaginated(FilterType.RATING, 3,3);

        // 3. Post-conditions
        assertEquals(2, artistList.size());
    }

    @Test
    public void test_findBySongId() {
        // 1. Pre-conditions - 5 artists exist in database

        // 2. Execute
        List<Artist> artistList = artistDao.findBySongId(PRE_EXISTING_SONG_ID);

        // 3. Post-conditions
        assertEquals(2, artistList.size());
    }

    @Test
    public void test_findBySongId_NoSong() {
        // 1. Pre-conditions - 5 artists exist in database

        // 2. Execute
        List<Artist> artistList = artistDao.findBySongId(NEW_SONG_ID);

        // 3. Post-conditions
        assertEquals(0, artistList.size());
    }

    @Test
    public void test_create() {
        // 1. Pre-conditions - the
        Artist artist = new Artist(NEW_ARTIST_NAME,NEW_ARTIST_BIO,PRE_EXISTING_IMAGE_ID);

        // 2. Execute
        Artist artistCreated = artistDao.create(artist);

        // 3. Post-conditions

        // Check if returned artist has expected value
        assertEquals(artist.getName(), artistCreated.getName());
        assertEquals(artist.getBio(), artistCreated.getBio());
        assertEquals(artist.getImgId(), artistCreated.getImgId());
        assertEquals(0, artistCreated.getAvgRating(),0);
        assertEquals(0, artistCreated.getRatingCount().intValue());

        // check if artist is saved correctly in database
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"artist",
                String.format("name = '%s' AND bio = '%s' AND img_id = '%d' AND avg_rating = 0 and rating_amount = 0",
                        NEW_ARTIST_NAME,
                        NEW_ARTIST_BIO,
                        PRE_EXISTING_IMAGE_ID)));
    }

    //@Test
    public void test_update() {
        // 1. Pre-conditions - the artist exist
        Artist artist = new Artist(PRE_EXISTING_ARTIST_ID, NEW_ARTIST_NAME, NEW_ARTIST_BIO, PRE_EXISTING_IMAGE_ID, NEW_ARTIST_RATING_AMOUNT, NEW_ARTIST_AVG_RATING);

        // 2. Execute
        Artist artistUpdated = artistDao.update(artist);

        // 3. Post-conditions

        // Check if returned artist has expected value
        assertEquals(artist.getName(), artistUpdated.getName());
        assertEquals(artist.getBio(), artistUpdated.getBio());
        assertEquals(artist.getImgId(), artistUpdated.getImgId());
        assertEquals(NEW_ARTIST_AVG_RATING, artistUpdated.getAvgRating(),0);
        assertEquals(NEW_ARTIST_RATING_AMOUNT, artistUpdated.getRatingCount().intValue());

        // Check if artist is saved correctly in database
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"artist",
                String.format("id = '%d' AND name = '%s' AND bio = '%s' AND img_id = '%d' AND ROUND(avg_rating, 1) = '%.1f' AND rating_amount = '%d'",
                        PRE_EXISTING_ARTIST_ID,
                        NEW_ARTIST_NAME,
                        NEW_ARTIST_BIO,
                        PRE_EXISTING_IMAGE_ID,
                        NEW_ARTIST_AVG_RATING,
                        NEW_ARTIST_RATING_AMOUNT)));
    }

    @Test
    public void test_delete() {
        // 1. Pre-conditions - the artist exist

        // 2. Execute
        boolean deleted = artistDao.delete(PRE_EXISTING_ARTIST_ID);

        // 3. Post-conditions
        assertTrue(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"artist",
                String.format("id = %d", PRE_EXISTING_ARTIST_ID)));
    }

    @Test
    public void test_delete_NoArtist() {
        // 1. Pre-conditions - the artist exist

        // 2. Execute
        boolean deleted = artistDao.delete(NEW_ARTIST_ID);

        // 3. Post-conditions
        assertFalse(deleted);
    }

    //@Test
    public void test_updateRating() {
        // 1. Pre-conditions - the artist exist

        // 2. Execute
        boolean updated = artistDao.updateRating(PRE_EXISTING_ARTIST_ID, NEW_ARTIST_AVG_RATING, NEW_ARTIST_RATING_AMOUNT);

        // 3. Post-conditions
        assertTrue(updated);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"artist",
                String.format("id = %d AND ROUND(avg_rating, 1) = '%.1f' AND rating_amount = '%d'",
                        PRE_EXISTING_ARTIST_ID,
                        NEW_ARTIST_AVG_RATING,
                        NEW_ARTIST_RATING_AMOUNT)));
    }

    @Test
    public void test_hasUserReviewed_Yes() {
        // 1. Pre-conditions - the user made a review about artist

        // 2. Execute
        boolean deleted = artistDao.hasUserReviewed(PRE_EXISTING_USER_ID, PRE_EXISTING_ARTIST_ID);

        // 3. Post-conditions
        assertTrue(deleted);
    }

    @Test
    public void test_hasUserReviewed_No() {
        // 1. Pre-conditions - the user did not make a review about artist

        // 2. Execute
        boolean deleted = artistDao.hasUserReviewed(PRE_EXISTING_USER_ID, PRE_EXISTING_ARTIST_2_ID);

        // 3. Post-conditions
        assertFalse(deleted);
    }


}
