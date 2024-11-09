package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
public class ArtistJpaDaoTest {

    private static final long PRE_EXISTING_IMAGE_ID = 100;
    private static final long PRE_EXISTING_USER_ID = 200;
    private static final long PRE_EXISTING_ARTIST_ID = 300;
    private static final long PRE_EXISTING_ARTIST_2_ID = 301;
    private static final long PRE_EXISTING_REVIEW_ID = 400;
    private static final long PRE_EXISTING_ALBUM_ID = 500;
    private static final long PRE_EXISTING_SONG_ID = 600;

    private static final long NEW_IMAGE_ID = 1000;
    private static final long NEW_ARTIST_ID = 1000;
    private static final long NEW_SONG_ID = 1000;

    private static final byte[] BYTES = new byte[] { (byte) 0xde, (byte) 0xad };

    private static final String PRE_EXISTING_ARTIST_NAME = "DummyName";
    private static final String PRE_EXISTING_ARTIST_BIO = "DummyBio";

    private static final String NEW_ARTIST_NAME = "DummyX";
    private static final String NEW_ARTIST_BIO = "This is a description of DummyX";
    private static final Double NEW_ARTIST_AVG_RATING = 3.4;
    private static final int NEW_ARTIST_RATING_AMOUNT = 15;

    @Autowired
    private ArtistJpaDao artistDao;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void test_find() {
        // 1. Pre-conditions - the artist exist

        // 2. Execute
        Optional<Artist> maybeArtist = artistDao.find(PRE_EXISTING_ARTIST_ID);

        // 3. Post-conditions
        assertTrue(maybeArtist.isPresent());
        assertEquals(PRE_EXISTING_ARTIST_ID, maybeArtist.get().getId().longValue());
        assertEquals(PRE_EXISTING_ARTIST_NAME, maybeArtist.get().getName());
        assertEquals(PRE_EXISTING_IMAGE_ID, maybeArtist.get().getImage().getId().longValue());
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
    public void test_findNameContaining() {
        // 1. Pre-conditions - artist exists containing substring

        // 2. Execute
        List<Artist> artistList = artistDao.findByNameContaining(PRE_EXISTING_ARTIST_NAME.substring(3,7)); // myNa

        // 3. Post-conditions
        assertEquals(4, artistList.size());
        for (Artist artist : artistList) {
            assertTrue(artist.getName().contains(PRE_EXISTING_ARTIST_NAME.substring(3,7)));
        }
    }

    @Test
    public void test_findByTitleContaining_NoArtist() {
        // 1. Pre-conditions - artist does not exist with substring

        // 2. Execute
        List<Artist> artistList = artistDao.findByNameContaining("Nothing"); // myNa

        // 3. Post-conditions
        assertEquals(0, artistList.size());
    }

    @Test
    public void test_create() {
        // 1. Pre-conditions - the
        Image image = new Image(NEW_IMAGE_ID, BYTES);
        Artist artist = new Artist(NEW_ARTIST_NAME, NEW_ARTIST_BIO, image, 0, 0.0);

        // 2. Execute
        Artist artistCreated = artistDao.create(artist);

        // 3. Post-conditions

        // Check if returned artist has expected value
        assertEquals(artist.getName(), artistCreated.getName());
        assertEquals(artist.getBio(), artistCreated.getBio());
        assertEquals(artist.getImage().getId(), artistCreated.getImage().getId());
        assertEquals(0, artistCreated.getRatingCount().intValue());
        assertEquals(0, artistCreated.getAvgRating(),0);

        // check if artist is saved correctly in database
        assertEquals(1,em.createQuery("SELECT COUNT(a) FROM Artist a " +
                                "JOIN a.image " +
                                "WHERE a.name = :name " +
                                "AND a.bio = :bio " +
                                "AND a.avgRating = 0 " +
                                "AND a.ratingCount = 0 " +
                                "AND a.image.id = :image_id",
                        Long.class)
                .setParameter("name", NEW_ARTIST_NAME)
                .setParameter("bio", NEW_ARTIST_BIO)
                .setParameter("image_id", NEW_IMAGE_ID)
                .getSingleResult().intValue());
    }

    @Test
    public void test_update() {
        // 1. Pre-conditions - the artist exist
        Image image = new Image(PRE_EXISTING_IMAGE_ID, BYTES);
        Artist artist = new Artist(PRE_EXISTING_ARTIST_ID, NEW_ARTIST_NAME, NEW_ARTIST_BIO, image, NEW_ARTIST_RATING_AMOUNT, NEW_ARTIST_AVG_RATING);

        // 2. Execute
        Artist artistUpdated = artistDao.update(artist);

        // 3. Post-conditions

        // Check if returned artist has expected value
        assertEquals(artist.getName(), artistUpdated.getName());
        assertEquals(artist.getBio(), artistUpdated.getBio());
        assertEquals(artist.getImage().getId(), artistUpdated.getImage().getId());
        assertEquals(NEW_ARTIST_AVG_RATING, artistUpdated.getAvgRating(),0);
        assertEquals(NEW_ARTIST_RATING_AMOUNT, artistUpdated.getRatingCount().intValue());

        // Check if artist is saved correctly in database
        assertEquals(1,em.createQuery("SELECT COUNT(a) FROM Artist a " +
                "JOIN a.image " +
                    "WHERE a.id = :artistId " +
                      "AND a.name = :name " +
                      "AND a.bio = :bio " +
                      "AND a.avgRating = :avgRating " +
                      "AND a.ratingCount = :ratingCount " +
                      "AND a.image.id = :image_id",
                Long.class)
                .setParameter("artistId", PRE_EXISTING_ARTIST_ID)
                .setParameter("name", NEW_ARTIST_NAME)
                .setParameter("bio", NEW_ARTIST_BIO)
                .setParameter("avgRating", NEW_ARTIST_AVG_RATING)
                .setParameter("ratingCount", NEW_ARTIST_RATING_AMOUNT)
                .setParameter("image_id", PRE_EXISTING_IMAGE_ID)
                .getSingleResult().intValue());
    }

    @Test
    public void test_updateRating() {
        // 1. Pre-conditions - the artist exist

        // 2. Execute
        boolean updated = artistDao.updateRating(PRE_EXISTING_ARTIST_ID, NEW_ARTIST_AVG_RATING, NEW_ARTIST_RATING_AMOUNT);

        // 3. Post-conditions
        assertTrue(updated);
        assertEquals(1,em.createQuery("SELECT COUNT(a) FROM Artist a " +
                                "WHERE a.id = :artistId " +
                                "AND a.avgRating = :avgRating " +
                                "AND a.ratingCount = :ratingCount",
                        Long.class)
                .setParameter("artistId", PRE_EXISTING_ARTIST_ID)
                .setParameter("avgRating", NEW_ARTIST_AVG_RATING)
                .setParameter("ratingCount", NEW_ARTIST_RATING_AMOUNT)
                .getSingleResult().intValue());
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

    @Test
    public void test_delete() {
        // 1. Pre-conditions - the artist exist

        // 2. Execute
        boolean deleted = artistDao.delete(PRE_EXISTING_ARTIST_ID);

        // 3. Post-conditions
        assertTrue(deleted);
        assertNull(em.find(Artist.class, PRE_EXISTING_ARTIST_ID));
        assertNull(em.find(Image.class, PRE_EXISTING_IMAGE_ID));
    }

    @Test
    public void test_delete_NoArtist() {
        // 1. Pre-conditions - the artist exist

        // 2. Execute
        boolean deleted = artistDao.delete(NEW_ARTIST_ID);

        // 3. Post-conditions
        assertFalse(deleted);
    }

    @Test
    public void test_deleteReviewsFromArtist_Yes() {
        // 1. Pre-conditions - artist has reviews

        // 2. Execute
        boolean deleted = artistDao.deleteReviewsFromArtist(PRE_EXISTING_ARTIST_ID);

        // 3. Post-conditions
        assertTrue(deleted);
    }

    @Test
    public void test_deleteReviewsFromArtist_No() {
        // 1. Pre-conditions - artist does not have reviews

        // 2. Execute
        boolean deleted = artistDao.deleteReviewsFromArtist(NEW_ARTIST_ID);

        // 3. Post-conditions
        assertFalse(deleted);
    }

    @Test
    public void test_findReviewsByArtistId() {
        // 1. Pre-conditions - artist has review

        // 2. Execute
        List<ArtistReview> artistReviewList = artistDao.findReviewsByArtistId(PRE_EXISTING_ARTIST_ID);

        // 3. Post-conditions
        assertEquals(1, artistReviewList.size());
    }

    @Test
    public void test_findReviewsByArtistId_NoReview() {
        // 1. Pre-conditions - artist has review

        // 2. Execute
        List<ArtistReview> artistReviewList = artistDao.findReviewsByArtistId(NEW_ARTIST_ID);

        // 3. Post-conditions
        assertEquals(0, artistReviewList.size());
    }
}
