package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.reviews.*;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "classpath:review_setUp.sql")
@Transactional
@Rollback
public class ReviewJdbcDaoTest {

    private static final long EXISTING_USER_ID = 1L;
    private static final long EXISTING_ARTIST_ID = 1L;
    private static final long EXISTING_ALBUM_ID = 1L;
    private static final long EXISTING_SONG_ID = 1L;
    private static final long EXISTING_REVIEW_ID = 1L;

    @Autowired
    private ReviewJdbcDao reviewDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testSaveArtistReview() {
        User user = new User(EXISTING_USER_ID, "username", "name", 1L, true, false);
        Artist artist = new Artist(EXISTING_ARTIST_ID, "Artist Name", 1L);
        ArtistReview review = new ArtistReview(user, artist, "Title", "Description", 5, LocalDateTime.now(), 0, false);

        ArtistReview savedReview = reviewDao.saveArtistReview(review);

        assertNotNull(savedReview.getId());
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "review",
                "id = " + savedReview.getId()));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "artist_review",
                "review_id = " + savedReview.getId() + " AND artist_id = " + EXISTING_ARTIST_ID));
    }

    @Test
    public void testSaveAlbumReview() {
        User user = new User(EXISTING_USER_ID, "username", "name", 1L, true, false);
        Artist artist = new Artist(EXISTING_ARTIST_ID);
        Album album = new Album(EXISTING_ALBUM_ID, "Album Title", 1L, "Genre", artist, LocalDate.now());
        AlbumReview review = new AlbumReview(user, album, "Title", "Description", 4, LocalDateTime.now(), 0, false);

        AlbumReview savedReview = reviewDao.saveAlbumReview(review);

        assertNotNull(savedReview.getId());
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "review",
                "id = " + savedReview.getId()));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "album_review",
                "review_id = " + savedReview.getId() + " AND album_id = " + EXISTING_ALBUM_ID));
    }

    @Test
    public void testSaveSongReview() {
        User user = new User(EXISTING_USER_ID, "username", "name", 1L, true, false);
        Artist artist = new Artist(EXISTING_ARTIST_ID);
        Album album = new Album(EXISTING_ALBUM_ID, "Album Title", 1L, "Genre", artist, LocalDate.now());
        Song song = new Song(EXISTING_SONG_ID, "Song Title", "3:30", album);
        SongReview review = new SongReview(user, song, "Title", "Description", 3, LocalDateTime.now(), 0, false);

        SongReview savedReview = reviewDao.saveSongReview(review);

        assertNotNull(savedReview.getId());
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "review",
                "id = " + savedReview.getId()));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "song_review",
                "review_id = " + savedReview.getId() + " AND song_id = " + EXISTING_SONG_ID));
    }

    @Test
    public void testUpdate() {
        Review review = reviewDao.find(EXISTING_REVIEW_ID).orElseThrow(RuntimeException::new);
        review.setTitle("Updated Title");
        review.setDescription("Updated Description");
        review.setRating(5);
        review.setLikes(10);

        Review updatedReview = reviewDao.update(review);

        assertEquals("Updated Title", updatedReview.getTitle());
        assertEquals("Updated Description", updatedReview.getDescription());
        assertEquals(5, updatedReview.getRating().intValue());
        assertEquals(10, updatedReview.getLikes().intValue());
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "review",
                "id = " + EXISTING_REVIEW_ID + " AND title = 'Updated Title' AND description = 'Updated Description' AND rating = 5 AND likes = 10"));
    }

    @Test
    public void testDelete() {
        boolean result = reviewDao.delete(EXISTING_REVIEW_ID);

        assertTrue(result);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "review",
                "id = " + EXISTING_REVIEW_ID));
    }

    @Test
    public void testCreateLike() {
        reviewDao.createLike(EXISTING_USER_ID, EXISTING_REVIEW_ID);

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "review_like",
                "user_id = " + EXISTING_USER_ID + " AND review_id = " + EXISTING_REVIEW_ID));
    }

    @Test
    public void testDeleteLike() {
        reviewDao.createLike(EXISTING_USER_ID, EXISTING_REVIEW_ID);
        reviewDao.deleteLike(EXISTING_USER_ID, EXISTING_REVIEW_ID);

        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "review_like",
                "user_id = " + EXISTING_USER_ID + " AND review_id = " + EXISTING_REVIEW_ID));
    }

    @Test
    public void testIncrementLikes() {
        Review review = reviewDao.find(EXISTING_REVIEW_ID).orElseThrow(RuntimeException::new);
        int initialLikes = review.getLikes();

        reviewDao.incrementLikes(EXISTING_REVIEW_ID);

        Review updatedReview = reviewDao.find(EXISTING_REVIEW_ID).orElseThrow(RuntimeException::new);
        assertEquals(initialLikes + 1, updatedReview.getLikes().intValue());
    }

    @Test
    public void testDecrementLikes() {
        Review review = reviewDao.find(EXISTING_REVIEW_ID).orElseThrow(RuntimeException::new);
        int initialLikes = review.getLikes();

        reviewDao.decrementLikes(EXISTING_REVIEW_ID);

        Review updatedReview = reviewDao.find(EXISTING_REVIEW_ID).orElseThrow(RuntimeException::new);
        assertEquals(initialLikes - 1, updatedReview.getLikes().intValue());
    }

    @Test
    public void testBlock() {
        reviewDao.block(EXISTING_REVIEW_ID);

        Review blockedReview = reviewDao.find(EXISTING_REVIEW_ID).orElseThrow(RuntimeException::new);
        assertTrue(blockedReview.isBlocked());
    }

    @Test
    public void testUnblock() {
        reviewDao.block(EXISTING_REVIEW_ID);
        reviewDao.unblock(EXISTING_REVIEW_ID);

        Review unblockedReview = reviewDao.find(EXISTING_REVIEW_ID).orElseThrow(RuntimeException::new);
        assertFalse(unblockedReview.isBlocked());
    }
}