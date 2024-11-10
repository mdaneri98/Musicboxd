package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.reviews.AlbumReview;
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
import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.*;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "classpath:album_setUp.sql")
public class AlbumJpaDaoTest {

    private static final long PRE_EXISTING_IMAGE_ID = 100;
    private static final long PRE_EXISTING_USER_ID = 200;
    private static final long PRE_EXISTING_ARTIST_ID = 300;
    private static final long PRE_EXISTING_ARTIST_2_ID = 301;
    private static final long PRE_EXISTING_REVIEW_ID = 400;
    private static final long PRE_EXISTING_ALBUM_ID = 500;
    private static final long PRE_EXISTING_ALBUM_2_ID = 501;
    private static final long PRE_EXISTING_SONG_ID = 600;

    private static final long NEW_IMAGE_ID = 1000;
    private static final long NEW_ARTIST_ID = 1000;
    private static final long NEW_ALBUM_ID = 1000;
    private static final long NEW_SONG_ID = 1000;

    private static final byte[] BYTES = new byte[] { (byte) 0xbe, (byte) 0xef };

    private static final String PRE_EXISTING_ARTIST_NAME = "DummyName";
    private static final String PRE_EXISTING_ARTIST_BIO = "DummyBio";

    private static final String PRE_EXISTING_ALBUM_TITLE = "DummyTitle";
    private static final String PRE_EXISTING_ALBUM_GENRE = "DummyGenre";
    private static final LocalDate PRE_EXISTING_ALBUM_RELEASE_DATE = LocalDate.parse("2000-10-10");

    private static final String NEW_ALBUM_TITLE = "DummyX";
    private static final String NEW_ALBUM_GENRE = "DummyX";
    private static final LocalDate NEW_ALBUM_RELEASE_DATE = LocalDate.parse("2015-11-15");
    private static final Double NEW_ALBUM_AVG_RATING = 3.4;
    private static final int NEW_ALBUM_RATING_AMOUNT = 15;

    @Autowired
    private AlbumJpaDao albumDao;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void test_find() {
        // 1. Pre-conditions - the album exist

        // 2. Execute
        Optional<Album> maybeAlbum = albumDao.find(PRE_EXISTING_ALBUM_ID);

        // 3. Post-conditions
        assertTrue(maybeAlbum.isPresent());
        assertEquals(PRE_EXISTING_ALBUM_ID, maybeAlbum.get().getId().longValue());
        assertEquals(PRE_EXISTING_ALBUM_TITLE, maybeAlbum.get().getTitle());
        assertEquals(PRE_EXISTING_ALBUM_GENRE, maybeAlbum.get().getGenre());
        assertEquals(PRE_EXISTING_ALBUM_RELEASE_DATE, maybeAlbum.get().getReleaseDate());
        assertEquals(PRE_EXISTING_IMAGE_ID, maybeAlbum.get().getImage().getId().longValue());
    }

    @Test(expected = NoSuchElementException.class)
    public void test_find_NoAlbum() {
        // 1. Pre-conditions - the album does not exist

        // 2. Execute
        Optional<Album> maybeAlbum = albumDao.find(NEW_ALBUM_ID);

        // 3. Post-conditions
        assertFalse(maybeAlbum.isPresent());
        assertEquals(NEW_ALBUM_ID, maybeAlbum.get().getId().longValue());
    }

    @Test
    public void test_findAll() {
        // 1. Pre-conditions - Only 5 albums exist in database

        // 2. Execute
        List<Album> albumList = albumDao.findAll();

        // 3. Post-conditions
        assertEquals(5, albumList.size());
    }

    @Test
    public void test_findPaginated() {
        // 1. Pre-conditions - 5 albums exist in database

        // 2. Execute
        List<Album> albumList = albumDao.findPaginated(FilterType.RATING, 3,1);

        // 3. Post-conditions
        assertEquals(3, albumList.size()); //Correct limit
        assertEquals(PRE_EXISTING_ALBUM_2_ID, albumList.getFirst().getId().longValue());  //Correct offset
    }

    @Test
    public void test_findPaginated_endPage() {
        // 1. Pre-conditions - 5 albums exist in database

        // 2. Execute
        List<Album> albumList = albumDao.findPaginated(FilterType.RATING, 3,3);

        // 3. Post-conditions
        assertEquals(2, albumList.size());
    }

    @Test
    public void test_findByTitleContaining() {
        // 1. Pre-conditions - album exists containing substring

        // 2 . Execute
        List<Album> albumList = albumDao.findByTitleContaining(PRE_EXISTING_ALBUM_TITLE.substring(3,7)); //myTi

        // 3. Post-conditions
        assertEquals(4, albumList.size());

        for(Album album : albumList) {
            assertTrue(album.getTitle().contains(PRE_EXISTING_ALBUM_TITLE.substring(3,7)));
        }
    }

    @Test
    public void test_findByTitleContaining_NoAlbum() {
        // 1. Pre-conditions - album does not exist with substring

        // 2 . Execute
        List<Album> albumList = albumDao.findByTitleContaining("Nothing");

        // 3. Post-conditions
        assertEquals(0, albumList.size());
    }

    @Test
    public void test_findByArtistId() {
        // 1. Pre-conditions - 4 albums exist in database from artist 301

        // 2. Execute
        List<Album> albumList = albumDao.findByArtistId(PRE_EXISTING_ARTIST_2_ID);

        // 3. Post-conditions
        assertEquals(4, albumList.size());
    }

    @Test
    public void test_findByArtistId_NoArtist() {
        // 1. Pre-conditions - artist does not exist

        // 2. Execute
        List<Album> albumList = albumDao.findByArtistId(NEW_ARTIST_ID);

        // 3. Post-conditions
        assertEquals(0, albumList.size());
    }

    @Test
    public void test_create() {
        // 1. Pre-conditions -
        Image image = new Image(NEW_IMAGE_ID, BYTES);
        Artist artist = new Artist(PRE_EXISTING_ARTIST_ID, PRE_EXISTING_ARTIST_NAME, PRE_EXISTING_ARTIST_BIO, image);
        Album album = new Album(NEW_ALBUM_TITLE, image, NEW_ALBUM_GENRE, artist, NEW_ALBUM_RELEASE_DATE, 0, 0.0);

        // 2. Execute
        Album albumCreated = albumDao.create(album);

        // 3. Post-conditions

        // Check if returned album has expected value
        assertEquals(album.getTitle(), albumCreated.getTitle());
        assertEquals(album.getGenre(), albumCreated.getGenre());
        assertEquals(album.getReleaseDate(), albumCreated.getReleaseDate());
        assertEquals(album.getImage().getId(), albumCreated.getImage().getId());
        assertEquals(PRE_EXISTING_ARTIST_ID, albumCreated.getArtist().getId().longValue());
        assertEquals(0, albumCreated.getAvgRating(),0);
        assertEquals(0, albumCreated.getRatingCount().intValue());

        // check if album is saved correctly in database
        assertEquals(1,em.createQuery("SELECT COUNT(a) FROM Album a " +
                "JOIN a.artist " +
                "JOIN a.image " +
                    "WHERE a.title = :title " +
                      "AND a.genre = :genre " +
                      "AND a.releaseDate = :releaseDate " +
                      "AND a.avgRating = 0 " +
                      "AND a.ratingCount = 0 " +
                      "AND a.image.id = :image_id " +
                      "AND a.artist.id = :artist_id",
                Long.class)
                    .setParameter("title", NEW_ALBUM_TITLE)
                    .setParameter("genre", NEW_ALBUM_GENRE)
                    .setParameter("releaseDate", NEW_ALBUM_RELEASE_DATE)
                    .setParameter("image_id", NEW_IMAGE_ID)
                    .setParameter("artist_id", PRE_EXISTING_ARTIST_ID)
                .getSingleResult().intValue());
    }

    @Test
    public void test_update() {
        // 1. Pre-conditions - the album exist
        Image image = new Image(PRE_EXISTING_IMAGE_ID, BYTES);
        Artist artist = new Artist(PRE_EXISTING_ARTIST_ID, PRE_EXISTING_ARTIST_NAME, PRE_EXISTING_ARTIST_BIO, image);
        Album album = new Album(PRE_EXISTING_ALBUM_ID, NEW_ALBUM_TITLE, NEW_ALBUM_GENRE, NEW_ALBUM_RELEASE_DATE, image, artist, NEW_ALBUM_RATING_AMOUNT, NEW_ALBUM_AVG_RATING);

        // 2. Execute
        Album albumUpdated = albumDao.update(album);

        // 3. Post-conditions

        // Check if returned album has expected value
        assertEquals(album.getTitle(), albumUpdated.getTitle());
        assertEquals(album.getGenre(), albumUpdated.getGenre());
        assertEquals(album.getReleaseDate(), albumUpdated.getReleaseDate());
        assertEquals(album.getImage().getId(), albumUpdated.getImage().getId());
        assertEquals(PRE_EXISTING_ARTIST_ID, albumUpdated.getArtist().getId().longValue());
        assertEquals(NEW_ALBUM_AVG_RATING, albumUpdated.getAvgRating(),0);
        assertEquals(NEW_ALBUM_RATING_AMOUNT, albumUpdated.getRatingCount().intValue());

        // Check if album is saved correctly in database
        assertEquals(1,em.createQuery("SELECT COUNT(a) FROM Album a " +
                "JOIN a.artist " +
                "JOIN a.image " +
                    "WHERE a.id = :albumId " +
                      "AND a.title = :title " +
                      "AND a.genre = :genre " +
                      "AND a.releaseDate = :releaseDate " +
                      "AND a.avgRating = :avgRating " +
                      "AND a.ratingCount = :ratingCount " +
                      "AND a.image.id = :image_id " +
                      "AND a.artist.id = :artist_id",
                Long.class)
                .setParameter("albumId", PRE_EXISTING_ALBUM_ID)
                .setParameter("title", NEW_ALBUM_TITLE)
                .setParameter("genre", NEW_ALBUM_GENRE)
                .setParameter("releaseDate", NEW_ALBUM_RELEASE_DATE)
                .setParameter("avgRating", NEW_ALBUM_AVG_RATING)
                .setParameter("ratingCount", NEW_ALBUM_RATING_AMOUNT)
                .setParameter("image_id", PRE_EXISTING_IMAGE_ID)
                .setParameter("artist_id", PRE_EXISTING_ARTIST_ID)
                .getSingleResult().intValue());
    }

    @Test
    public void test_updateRating() {
        // 1. Pre-conditions - the album exist

        // 2. Execute
        boolean updated = albumDao.updateRating(PRE_EXISTING_ALBUM_ID, NEW_ALBUM_AVG_RATING, NEW_ALBUM_RATING_AMOUNT);

        // 3. Post-conditions
        assertTrue(updated);
        assertEquals(1,em.createQuery("SELECT COUNT(a) FROM Album a " +
                                "WHERE a.id = :albumId " +
                                "AND a.avgRating = :avgRating " +
                                "AND a.ratingCount = :ratingCount",
                        Long.class)
                .setParameter("albumId", PRE_EXISTING_ALBUM_ID)
                .setParameter("avgRating", NEW_ALBUM_AVG_RATING)
                .setParameter("ratingCount", NEW_ALBUM_RATING_AMOUNT)
                .getSingleResult().intValue());
    }

    @Test
    public void test_hasUserReviewed_Yes() {
        // 1. Pre-conditions - the user made a review about album

        // 2. Execute
        boolean reviewed = albumDao.hasUserReviewed(PRE_EXISTING_USER_ID, PRE_EXISTING_ALBUM_ID);

        // 3. Post-conditions
        assertTrue(reviewed);
    }

    @Test
    public void test_hasUserReviewed_No() {
        // 1. Pre-conditions - the user did not make a review about album

        // 2. Execute
        boolean deleted = albumDao.hasUserReviewed(PRE_EXISTING_USER_ID, PRE_EXISTING_ALBUM_2_ID);

        // 3. Post-conditions
        assertFalse(deleted);
    }

    @Test
    public void test_delete() {
        // 1. Pre-conditions - the album exist

        // 2. Execute
        boolean deleted = albumDao.delete(PRE_EXISTING_ALBUM_ID);

        // 3. Post-conditions
        assertTrue(deleted);
        assertNull(em.find(Album.class, PRE_EXISTING_ALBUM_ID));
        assertNull(em.find(Image.class, PRE_EXISTING_IMAGE_ID));
    }

    @Test
    public void test_delete_NoAlbum() {
        // 1. Pre-conditions - the album does not exist

        // 2. Execute
        boolean deleted = albumDao.delete(NEW_ALBUM_ID);

        // 3. Post-conditions
        assertFalse(deleted);
    }

    @Test
    public void test_deleteReviewsFromAlbum_Yes() {
        // 1. Pre-conditions - album has reviews

        // 2. Execute
        boolean deleted = albumDao.deleteReviewsFromAlbum(PRE_EXISTING_ALBUM_ID);

        // 3. Post-conditions
        assertTrue(deleted);
    }

    @Test
    public void test_deleteReviewsFromAlbum_No() {
        // 1. Pre-conditions - album does not have reviews

        // 2. Execute
        boolean deleted = albumDao.deleteReviewsFromAlbum(NEW_ALBUM_ID);

        // 3. Post-conditions
        assertFalse(deleted);
    }

    @Test
    public void test_findReviewsByAlbumId() {
        // 1. Pre-conditions - album has review

        // 2. Execute
        List<AlbumReview> albumReviewList = albumDao.findReviewsByAlbumId(PRE_EXISTING_ALBUM_ID);

        // 3. Post-conditions
        assertEquals(1, albumReviewList.size());
    }

    @Test
    public void test_findReviewsByAlbumId_NoReview() {
        // 1. Pre-conditions - album has review

        // 2. Execute
        List<AlbumReview> albumReviewList = albumDao.findReviewsByAlbumId(NEW_ALBUM_ID);

        // 3. Post-conditions
        assertEquals(0, albumReviewList.size());
    }
}
