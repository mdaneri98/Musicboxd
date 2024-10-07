package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.Review;
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
import java.util.Optional;

import static org.junit.Assert.*;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "classpath:review_setUp.sql")
public class ReviewJdbcDaoTest {

    private static final long PRE_EXISTING_USER_ID = 200;
    private static final long PRE_EXISTING_ARTIST_ID = 300;
    private static final long PRE_EXISTING_REVIEW_ID = 400;

    private static final String PRE_EXISTING_USERNAME = "DummyUsername";
    private static final String PRE_EXISTING_EMAIL = "DummyEmail";
    private static final String PRE_EXISTING_PASSWORD = "DummyPassword";

    private static final String NEW_TITLE = "DummyTitle";
    private static final String NEW_DESCRIPTION = "DummyDescription";
    private static final int NEW_RATING = 4;
    private static final int NEW_LIKES = 7;
    private static final boolean IS_BLOCKED = false;

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
    public void test_update() {
        // 1. Pre-conditions - user, artist and review exist
        User user = new User(PRE_EXISTING_USER_ID, PRE_EXISTING_USERNAME, PRE_EXISTING_EMAIL, PRE_EXISTING_PASSWORD);
        Artist artist = new Artist(PRE_EXISTING_ARTIST_ID);
        Review review = new ArtistReview(PRE_EXISTING_REVIEW_ID, artist, user, NEW_TITLE, NEW_DESCRIPTION, NEW_RATING, null,  NEW_LIKES, IS_BLOCKED);

        // 2. Execute
        Review updateReview = reviewDao.update(review);

        // 3. Post-conditions
        assertEquals(review.getTitle(), updateReview.getTitle());
        assertEquals(review.getDescription(), updateReview.getDescription());
        assertEquals(review.getRating(), updateReview.getRating());
        assertEquals(NEW_LIKES, updateReview.getLikes().intValue());
        assertFalse(updateReview.isBlocked());

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"review",
                String.format("id = '%d' AND user_id = '%d' AND title = '%s' AND description = '%s' AND rating = '%d' AND likes = '%d' AND isblocked = '%s'",
                        PRE_EXISTING_REVIEW_ID,
                        PRE_EXISTING_USER_ID,
                        NEW_TITLE,
                        NEW_DESCRIPTION,
                        NEW_RATING,
                        NEW_LIKES,
                        IS_BLOCKED)));
    }

    @Test
    public void test_() {
        // 1. Pre-conditions -


        // 2. Execute


        // 3. Post-conditions
    }
}
