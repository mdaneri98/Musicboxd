package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.SongReview;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "classpath:review_setUp.sql")
@Transactional
@Rollback
public class ReviewJpaDaoTest {

    private static final long PRE_EXISTING_USER_ID = 200;
    private static final long PRE_EXISTING_USER_2_ID = 201;
    private static final long PRE_EXISTING_ARTIST_ID = 300;
    private static final long PRE_EXISTING_ARTIST_2_ID = 301;
    private static final long PRE_EXISTING_ALBUM_ID = 500;
    private static final long PRE_EXISTING_ALBUM_2_ID = 501;
    private static final long PRE_EXISTING_SONG_ID = 600;
    private static final long PRE_EXISTING_SONG_2_ID = 601;

    private static final long PRE_EXISTING_REVIEW_ID = 400;
    private static final long PRE_EXISTING_REVIEW_2_ID = 401;
    private static final long PRE_EXISTING_REVIEW_3_ID = 402;
    private static final long BLOCKED_REVIEW_ID = 403;
    private static final long NEW_REVIEW_ID = 1000;

    private static final String NEW_TITLE = "Updated Title";
    private static final String NEW_DESCRIPTION = "Updated Description";
    private static final int NEW_RATING = 4;
    private static final int NEW_LIKES_AMOUNT = 10;
    private static final boolean IS_BLOCKED_TRUE = true;
    private static final int NEW_COMMENT_AMOUNT = 5;
    private static final String NEW_COMMENT = "New Comment";

    @Autowired
    ReviewJpaDao reviewDao;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void test_create() {
        // 1. Pre-conditions
        User user = em.find(User.class, PRE_EXISTING_USER_ID);
        Artist artist = em.find(Artist.class, PRE_EXISTING_ARTIST_ID);
        LocalDateTime now = LocalDateTime.now();

        ArtistReview review = new ArtistReview(user, artist, NEW_TITLE, NEW_DESCRIPTION, NEW_RATING, now, 0, false, 0);

        // 2. Execute
        Review created = reviewDao.create(review);

        // 3. Post-conditions
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(NEW_TITLE, created.getTitle());
        assertEquals(NEW_DESCRIPTION, created.getDescription());
        assertEquals(NEW_RATING, created.getRating().intValue());
        assertEquals(0, created.getLikes().intValue());
        assertEquals(0, created.getCommentAmount().intValue());
        assertFalse(created.isBlocked());
        assertEquals(now, created.getCreatedAt());
        assertEquals(user.getId(), created.getUser().getId());

        // Verify in database
        Review dbReview = em.find(Review.class, created.getId());
        assertNotNull(dbReview);
        assertEquals(created.getTitle(), dbReview.getTitle());
        assertEquals(created.getDescription(), dbReview.getDescription());
        assertEquals(created.getRating(), dbReview.getRating());
        assertEquals(created.getLikes(), dbReview.getLikes());
        assertEquals(created.getCommentAmount(), dbReview.getCommentAmount());
        assertEquals(created.isBlocked(), dbReview.isBlocked());
        assertEquals(created.getCreatedAt(), dbReview.getCreatedAt());
        assertEquals(created.getUser().getId(), dbReview.getUser().getId());
    }

    @Test
    public void test_update() {
        // 1. Pre-conditions
        Review review = em.find(Review.class, PRE_EXISTING_REVIEW_ID);
        review.setTitle(NEW_TITLE);
        review.setDescription(NEW_DESCRIPTION);
        review.setRating(NEW_RATING);
        review.setLikes(NEW_LIKES_AMOUNT);
        review.setBlocked(IS_BLOCKED_TRUE);
        review.setCommentAmount(NEW_COMMENT_AMOUNT);

        // 2. Execute
        Review updated = reviewDao.update(review);

        // 3. Post-conditions
        assertNotNull(updated);
        assertEquals(NEW_TITLE, updated.getTitle());
        assertEquals(NEW_DESCRIPTION, updated.getDescription());
        assertEquals(NEW_RATING, updated.getRating().intValue());
        assertEquals(NEW_LIKES_AMOUNT, updated.getLikes().intValue());
        assertEquals(NEW_COMMENT_AMOUNT, updated.getCommentAmount().intValue());
        assertTrue(updated.isBlocked());

        // Verify in database
        Review dbReview = em.find(Review.class, PRE_EXISTING_REVIEW_ID);
        assertEquals(updated.getTitle(), dbReview.getTitle());
        assertEquals(updated.getDescription(), dbReview.getDescription());
        assertEquals(updated.getRating(), dbReview.getRating());
        assertEquals(updated.getLikes(), dbReview.getLikes());
        assertEquals(updated.getCommentAmount(), dbReview.getCommentAmount());
        assertEquals(updated.isBlocked(), dbReview.isBlocked());
    }

    @Test
    public void test_delete_ExistingReview() {
        // 1. Pre-conditions

        // 2. Execute
        boolean deleted = reviewDao.delete(PRE_EXISTING_REVIEW_ID);

        // 3. Post-conditions
        assertTrue(deleted);
        assertNull(em.find(Review.class, PRE_EXISTING_REVIEW_ID));

        // Verify likes were deleted
        assertEquals(0L, em.createQuery("SELECT COUNT(rl) FROM Review r JOIN r.likedBy rl WHERE r.id = :reviewId")
                .setParameter("reviewId", PRE_EXISTING_REVIEW_ID)
                .getSingleResult());

        // Verify comments were deleted
        assertEquals(0L, em.createQuery("SELECT COUNT(c) FROM Comment c WHERE c.review.id = :reviewId")
                .setParameter("reviewId", PRE_EXISTING_REVIEW_ID)
                .getSingleResult());
    }

    @Test
    public void test_delete_NonExistingReview() {
        // 1. Pre-conditions
        assertNull(em.find(Review.class, NEW_REVIEW_ID));

        // 2. Execute
        boolean deleted = reviewDao.delete(NEW_REVIEW_ID);

        // 3. Post-conditions
        assertFalse(deleted);
    }

    @Test
    public void test_findArtistReviewById_Exists() {
        // 1. Pre-conditions - review exists

        // 2. Execute
        Optional<ArtistReview> maybeReview = reviewDao.findArtistReviewById(PRE_EXISTING_REVIEW_ID);

        // 3. Post-conditions
        assertTrue(maybeReview.isPresent());
        ArtistReview review = maybeReview.get();
        assertEquals(PRE_EXISTING_REVIEW_ID, review.getId().longValue());
        assertEquals(PRE_EXISTING_USER_ID, review.getUser().getId().longValue());
        assertEquals(PRE_EXISTING_ARTIST_ID, review.getArtist().getId().longValue());
        assertEquals("Artist Review 1", review.getTitle());
        assertEquals("Great artist", review.getDescription());
        assertEquals(5, review.getRating().intValue());
        assertEquals(3, review.getLikes().intValue());
        assertEquals(2, review.getCommentAmount().intValue());
        assertFalse(review.isBlocked());
    }

    @Test
    public void test_findArtistReviewById_NotExists() {
        // 1. Pre-conditions - review doesn't exist

        // 2. Execute
        Optional<ArtistReview> maybeReview = reviewDao.findArtistReviewById(NEW_REVIEW_ID);

        // 3. Post-conditions
        assertFalse(maybeReview.isPresent());
    }

    @Test
    public void test_findAlbumReviewById_Exists() {
        // 1. Pre-conditions - review exists

        // 2. Execute
        Optional<AlbumReview> maybeReview = reviewDao.findAlbumReviewById(PRE_EXISTING_REVIEW_2_ID);

        // 3. Post-conditions
        assertTrue(maybeReview.isPresent());
        AlbumReview review = maybeReview.get();
        assertEquals(PRE_EXISTING_REVIEW_2_ID, review.getId().longValue());
        assertEquals(PRE_EXISTING_USER_ID, review.getUser().getId().longValue());
        assertEquals(PRE_EXISTING_ALBUM_ID, review.getAlbum().getId().longValue());
        assertEquals("Album Review 1", review.getTitle());
        assertEquals("Amazing album", review.getDescription());
        assertEquals(4, review.getRating().intValue());
        assertEquals(2, review.getLikes().intValue());
        assertEquals(1, review.getCommentAmount().intValue());
        assertFalse(review.isBlocked());
    }

    @Test
    public void test_findAlbumReviewById_NotExists() {
        // 1. Pre-conditions - review doesn't exist

        // 2. Execute
        Optional<AlbumReview> maybeReview = reviewDao.findAlbumReviewById(NEW_REVIEW_ID);

        // 3. Post-conditions
        assertFalse(maybeReview.isPresent());
    }

    @Test
    public void test_findSongReviewById_Exists() {
        // 1. Pre-conditions - review exists

        // 2. Execute
        Optional<SongReview> maybeReview = reviewDao.findSongReviewById(PRE_EXISTING_REVIEW_3_ID);

        // 3. Post-conditions
        assertTrue(maybeReview.isPresent());
        SongReview review = maybeReview.get();
        assertEquals(PRE_EXISTING_REVIEW_3_ID, review.getId().longValue());
        assertEquals(PRE_EXISTING_USER_ID, review.getUser().getId().longValue());
        assertEquals(PRE_EXISTING_SONG_ID, review.getSong().getId().longValue());
        assertEquals("Song Review 1", review.getTitle());
        assertEquals("Nice song", review.getDescription());
        assertEquals(3, review.getRating().intValue());
        assertEquals(1, review.getLikes().intValue());
        assertEquals(0, review.getCommentAmount().intValue());
        assertFalse(review.isBlocked());
    }

    @Test
    public void test_findSongReviewById_NotExists() {
        // 1. Pre-conditions - review doesn't exist

        // 2. Execute
        Optional<SongReview> maybeReview = reviewDao.findSongReviewById(NEW_REVIEW_ID);

        // 3. Post-conditions
        assertFalse(maybeReview.isPresent());
    }

    @Test
    public void test_findArtistReviewByUserId_Exists() {
        // 1. Pre-conditions - review exists

        // 2. Execute
        Optional<ArtistReview> maybeReview = reviewDao.findArtistReviewByUserId(PRE_EXISTING_USER_ID, PRE_EXISTING_ARTIST_ID);

        // 3. Post-conditions
        assertTrue(maybeReview.isPresent());
        ArtistReview review = maybeReview.get();
        assertEquals(PRE_EXISTING_REVIEW_ID, review.getId().longValue());
        assertEquals(PRE_EXISTING_USER_ID, review.getUser().getId().longValue());
        assertEquals(PRE_EXISTING_ARTIST_ID, review.getArtist().getId().longValue());
    }

    @Test
    public void test_findArtistReviewByUserId_NotExists() {
        // 1. Pre-conditions - review doesn't exist

        // 2. Execute
        Optional<ArtistReview> maybeReview = reviewDao.findArtistReviewByUserId(NEW_REVIEW_ID, PRE_EXISTING_ARTIST_ID);

        // 3. Post-conditions
        assertFalse(maybeReview.isPresent());
    }

    @Test
    public void test_findArtistReviewByUserId_Blocked() {
        // 1. Pre-conditions - review exists but is blocked

        // 2. Execute
        Optional<ArtistReview> maybeReview = reviewDao.findArtistReviewByUserId(PRE_EXISTING_USER_2_ID, PRE_EXISTING_ARTIST_2_ID);

        // 3. Post-conditions
        assertFalse(maybeReview.isPresent());
    }

    @Test
    public void test_findAlbumReviewByUserId_Exists() {
        // 1. Pre-conditions - review exists

        // 2. Execute
        Optional<AlbumReview> maybeReview = reviewDao.findAlbumReviewByUserId(PRE_EXISTING_USER_ID, PRE_EXISTING_ALBUM_ID);

        // 3. Post-conditions
        assertTrue(maybeReview.isPresent());
        AlbumReview review = maybeReview.get();
        assertEquals(PRE_EXISTING_REVIEW_2_ID, review.getId().longValue());
        assertEquals(PRE_EXISTING_USER_ID, review.getUser().getId().longValue());
        assertEquals(PRE_EXISTING_ALBUM_ID, review.getAlbum().getId().longValue());
    }

    @Test
    public void test_findAlbumReviewByUserId_NotExists() {
        // 1. Pre-conditions - review doesn't exist

        // 2. Execute
        Optional<AlbumReview> maybeReview = reviewDao.findAlbumReviewByUserId(NEW_REVIEW_ID, PRE_EXISTING_ALBUM_ID);

        // 3. Post-conditions
        assertFalse(maybeReview.isPresent());
    }

    @Test
    public void test_findSongReviewByUserId_Exists() {
        // 1. Pre-conditions - review exists

        // 2. Execute
        Optional<SongReview> maybeReview = reviewDao.findSongReviewByUserId(PRE_EXISTING_USER_ID, PRE_EXISTING_SONG_ID);

        // 3. Post-conditions
        assertTrue(maybeReview.isPresent());
        SongReview review = maybeReview.get();
        assertEquals(PRE_EXISTING_REVIEW_3_ID, review.getId().longValue());
        assertEquals(PRE_EXISTING_USER_ID, review.getUser().getId().longValue());
        assertEquals(PRE_EXISTING_SONG_ID, review.getSong().getId().longValue());
    }

    @Test
    public void test_findSongReviewByUserId_NotExists() {
        // 1. Pre-conditions - review doesn't exist

        // 2. Execute
        Optional<SongReview> maybeReview = reviewDao.findSongReviewByUserId(NEW_REVIEW_ID, PRE_EXISTING_SONG_ID);

        // 3. Post-conditions
        assertFalse(maybeReview.isPresent());
    }

    @Test
    public void test_saveArtistReview_New() {
        // 1. Pre-conditions
        User user = em.find(User.class, PRE_EXISTING_USER_ID);
        Artist artist = em.find(Artist.class, PRE_EXISTING_ARTIST_2_ID);
        ArtistReview review = new ArtistReview(user, artist,NEW_TITLE, NEW_DESCRIPTION, NEW_RATING, LocalDateTime.now(), 0, false, 0);

        // 2. Execute
        ArtistReview saved = reviewDao.saveArtistReview(review);

        // 3. Post-conditions
        assertNotNull(saved.getId());
        assertEquals(NEW_TITLE, saved.getTitle());
        assertEquals(NEW_DESCRIPTION, saved.getDescription());
        assertEquals(NEW_RATING, saved.getRating().intValue());
        assertEquals(0, saved.getLikes().intValue());
        assertEquals(0, saved.getCommentAmount().intValue());
        assertFalse(saved.isBlocked());
        assertEquals(PRE_EXISTING_ARTIST_2_ID, saved.getArtist().getId().longValue());

        // Verify in database
        ArtistReview dbReview = em.find(ArtistReview.class, saved.getId());
        assertNotNull(dbReview);
        assertEquals(saved.getTitle(), dbReview.getTitle());
        assertEquals(saved.getDescription(), dbReview.getDescription());
        assertEquals(saved.getRating(), dbReview.getRating());
        assertEquals(saved.getArtist().getId(), dbReview.getArtist().getId());
    }

    @Test
    public void test_saveArtistReview_Update() {
        // 1. Pre-conditions
        ArtistReview review = em.find(ArtistReview.class, PRE_EXISTING_REVIEW_ID);
        review.setTitle(NEW_TITLE);
        review.setDescription(NEW_DESCRIPTION);
        review.setRating(NEW_RATING);

        // 2. Execute
        ArtistReview updated = reviewDao.saveArtistReview(review);

        // 3. Post-conditions
        assertEquals(PRE_EXISTING_REVIEW_ID, updated.getId().longValue());
        assertEquals(NEW_TITLE, updated.getTitle());
        assertEquals(NEW_DESCRIPTION, updated.getDescription());
        assertEquals(NEW_RATING, updated.getRating().intValue());

        // Verify in database
        ArtistReview dbReview = em.find(ArtistReview.class, PRE_EXISTING_REVIEW_ID);
        assertEquals(updated.getTitle(), dbReview.getTitle());
        assertEquals(updated.getDescription(), dbReview.getDescription());
        assertEquals(updated.getRating(), dbReview.getRating());
    }

    @Test
    public void test_saveAlbumReview_New() {
        // 1. Pre-conditions
        User user = em.find(User.class, PRE_EXISTING_USER_ID);
        Album album = em.find(Album.class, PRE_EXISTING_ALBUM_2_ID);
        AlbumReview review = new AlbumReview(user, album, NEW_TITLE, NEW_DESCRIPTION, NEW_RATING, LocalDateTime.now(), 0, false, 0);

        // 2. Execute
        AlbumReview saved = reviewDao.saveAlbumReview(review);

        // 3. Post-conditions
        assertNotNull(saved.getId());
        assertEquals(NEW_TITLE, saved.getTitle());
        assertEquals(NEW_DESCRIPTION, saved.getDescription());
        assertEquals(NEW_RATING, saved.getRating().intValue());
        assertEquals(0, saved.getLikes().intValue());
        assertEquals(0, saved.getCommentAmount().intValue());
        assertFalse(saved.isBlocked());
        assertEquals(PRE_EXISTING_ALBUM_2_ID, saved.getAlbum().getId().longValue());

        // Verify in database
        AlbumReview dbReview = em.find(AlbumReview.class, saved.getId());
        assertNotNull(dbReview);
        assertEquals(saved.getTitle(), dbReview.getTitle());
        assertEquals(saved.getDescription(), dbReview.getDescription());
        assertEquals(saved.getRating(), dbReview.getRating());
        assertEquals(saved.getAlbum().getId(), dbReview.getAlbum().getId());
    }

    @Test
    public void test_saveAlbumReview_Update() {
        // 1. Pre-conditions
        AlbumReview review = em.find(AlbumReview.class, PRE_EXISTING_REVIEW_2_ID);
        review.setTitle(NEW_TITLE);
        review.setDescription(NEW_DESCRIPTION);
        review.setRating(NEW_RATING);

        // 2. Execute
        AlbumReview updated = reviewDao.saveAlbumReview(review);

        // 3. Post-conditions
        assertEquals(PRE_EXISTING_REVIEW_2_ID, updated.getId().longValue());
        assertEquals(NEW_TITLE, updated.getTitle());
        assertEquals(NEW_DESCRIPTION, updated.getDescription());
        assertEquals(NEW_RATING, updated.getRating().intValue());

        // Verify in database
        AlbumReview dbReview = em.find(AlbumReview.class, PRE_EXISTING_REVIEW_2_ID);
        assertEquals(updated.getTitle(), dbReview.getTitle());
        assertEquals(updated.getDescription(), dbReview.getDescription());
        assertEquals(updated.getRating(), dbReview.getRating());
    }

    @Test
    public void test_saveSongReview_New() {
        // 1. Pre-conditions
        User user = em.find(User.class, PRE_EXISTING_USER_ID);
        Song song = em.find(Song.class, PRE_EXISTING_SONG_2_ID);
        SongReview review = new SongReview(user, song, NEW_TITLE, NEW_DESCRIPTION, NEW_RATING, LocalDateTime.now(), 0, false, 0);

        // 2. Execute
        SongReview saved = reviewDao.saveSongReview(review);

        // 3. Post-conditions
        assertNotNull(saved.getId());
        assertEquals(NEW_TITLE, saved.getTitle());
        assertEquals(NEW_DESCRIPTION, saved.getDescription());
        assertEquals(NEW_RATING, saved.getRating().intValue());
        assertEquals(0, saved.getLikes().intValue());
        assertEquals(0, saved.getCommentAmount().intValue());
        assertFalse(saved.isBlocked());
        assertEquals(PRE_EXISTING_SONG_2_ID, saved.getSong().getId().longValue());

        // Verify in database
        SongReview dbReview = em.find(SongReview.class, saved.getId());
        assertNotNull(dbReview);
        assertEquals(saved.getTitle(), dbReview.getTitle());
        assertEquals(saved.getDescription(), dbReview.getDescription());
        assertEquals(saved.getRating(), dbReview.getRating());
        assertEquals(saved.getSong().getId(), dbReview.getSong().getId());
    }

    @Test
    public void test_saveSongReview_Update() {
        // 1. Pre-conditions
        SongReview review = em.find(SongReview.class, PRE_EXISTING_REVIEW_3_ID);
        review.setTitle(NEW_TITLE);
        review.setDescription(NEW_DESCRIPTION);
        review.setRating(NEW_RATING);

        // 2. Execute
        SongReview updated = reviewDao.saveSongReview(review);

        // 3. Post-conditions
        assertEquals(PRE_EXISTING_REVIEW_3_ID, updated.getId().longValue());
        assertEquals(NEW_TITLE, updated.getTitle());
        assertEquals(NEW_DESCRIPTION, updated.getDescription());
        assertEquals(NEW_RATING, updated.getRating().intValue());

        // Verify in database
        SongReview dbReview = em.find(SongReview.class, PRE_EXISTING_REVIEW_3_ID);
        assertEquals(updated.getTitle(), dbReview.getTitle());
        assertEquals(updated.getDescription(), dbReview.getDescription());
        assertEquals(updated.getRating(), dbReview.getRating());
    }

    @Test
    public void test_block() {
        // 1. Pre-conditions
        Review review = em.find(Review.class, PRE_EXISTING_REVIEW_ID);
        assertFalse(review.isBlocked());

        // 2. Execute
        reviewDao.block(PRE_EXISTING_REVIEW_ID);

        // 3. Post-conditions
        em.flush();
        em.clear();

        Review blockedReview = em.find(Review.class, PRE_EXISTING_REVIEW_ID);
        assertTrue(blockedReview.isBlocked());
    }

    @Test
    public void test_unblock() {
        // 1. Pre-conditions
        Review review = em.find(Review.class, BLOCKED_REVIEW_ID);
        assertTrue(review.isBlocked());

        // 2. Execute
        reviewDao.unblock(BLOCKED_REVIEW_ID);

        // 3. Post-conditions
        em.flush();
        em.clear();

        Review unblockedReview = em.find(Review.class, BLOCKED_REVIEW_ID);
        assertFalse(unblockedReview.isBlocked());
    }

    @Test
    public void test_isArtistReview_True() {
        // 1. Pre-conditions - review exists and is artist review

        // 2. Execute
        boolean isArtistReview = reviewDao.isArtistReview(PRE_EXISTING_REVIEW_ID);

        // 3. Post-conditions
        assertTrue(isArtistReview);
    }

    @Test
    public void test_isArtistReview_False() {
        // 1. Pre-conditions - review exists but is not artist review

        // 2. Execute
        boolean isArtistReview = reviewDao.isArtistReview(PRE_EXISTING_REVIEW_2_ID);

        // 3. Post-conditions
        assertFalse(isArtistReview);
    }

    @Test
    public void test_isAlbumReview_True() {
        // 1. Pre-conditions - review exists and is album review

        // 2. Execute
        boolean isAlbumReview = reviewDao.isAlbumReview(PRE_EXISTING_REVIEW_2_ID);

        // 3. Post-conditions
        assertTrue(isAlbumReview);
    }

    @Test
    public void test_isAlbumReview_False() {
        // 1. Pre-conditions - review exists but is not album review

        // 2. Execute
        boolean isAlbumReview = reviewDao.isAlbumReview(PRE_EXISTING_REVIEW_ID);

        // 3. Post-conditions
        assertFalse(isAlbumReview);
    }

    @Test
    public void test_isSongReview_True() {
        // 1. Pre-conditions - review exists and is song review

        // 2. Execute
        boolean isSongReview = reviewDao.isSongReview(PRE_EXISTING_REVIEW_3_ID);

        // 3. Post-conditions
        assertTrue(isSongReview);
    }

    @Test
    public void test_isSongReview_False() {
        // 1. Pre-conditions - review exists but is not song review

        // 2. Execute
        boolean isSongReview = reviewDao.isSongReview(PRE_EXISTING_REVIEW_ID);

        // 3. Post-conditions
        assertFalse(isSongReview);
    }

    @Test
    public void test_findAll() {
        // 1. Pre-conditions - multiple reviews exist

        // 2. Execute
        List<Review> reviews = reviewDao.findAll();

        // 3. Post-conditions
        assertNotNull(reviews);
        assertEquals(5, reviews.size()); // All non-blocked reviews

        // Verify reviews are ordered by creation date (DESC)
        LocalDateTime previousDate = LocalDateTime.now();
        for(Review review : reviews) {
            assertTrue(review.getCreatedAt().isBefore(previousDate) || review.getCreatedAt().equals(previousDate));
            previousDate = review.getCreatedAt();
            assertFalse(review.isBlocked());
        }
    }

    @Test
    public void test_findPaginated() {
        // 1. Pre-conditions
        int limit = 2;
        int offset = 0;

        // 2. Execute
        List<Review> reviews = reviewDao.findPaginated(FilterType.NEWEST, limit, offset);

        // 3. Post-conditions
        assertNotNull(reviews);
        assertEquals(2, reviews.size());

        // Verify reviews are ordered by creation date (DESC)
        assertTrue(reviews.get(0).getCreatedAt().isAfter(reviews.get(1).getCreatedAt()));

        // Verify no blocked reviews are returned
        for(Review review : reviews) {
            assertFalse(review.isBlocked());
        }
    }

    @Test
    public void test_findPaginated_Likes() {
        // 1. Pre-conditions
        int limit = 2;
        int offset = 0;

        // 2. Execute
        List<Review> reviews = reviewDao.findPaginated(FilterType.LIKES, limit, offset);

        // 3. Post-conditions
        assertNotNull(reviews);
        assertFalse(reviews.isEmpty());
        assertTrue(reviews.size() <= limit);
        
        // Verify reviews are ordered by likes (DESC)
        for (int i = 0; i < reviews.size() - 1; i++) {
            assertTrue(reviews.get(i).getLikes() >= reviews.get(i + 1).getLikes());
        }
        
        // Verify no blocked reviews are returned
        for(Review review : reviews) {
            assertFalse(review.isBlocked());
        }
    }

    @Test
    public void test_findPaginated_WithOffset() {
        // 1. Pre-conditions
        int limit = 2;
        int offset = 2;

        // 2. Execute
        List<Review> reviews = reviewDao.findPaginated(FilterType.NEWEST, limit, offset);

        // 3. Post-conditions
        assertNotNull(reviews);
        assertFalse(reviews.isEmpty());
        assertTrue(reviews.size() <= limit);

        // Verify reviews are ordered by creation date (DESC)
        for (int i = 0; i < reviews.size() - 1; i++) {
            assertTrue(reviews.get(i).getCreatedAt().isAfter(reviews.get(i + 1).getCreatedAt())
                    || reviews.get(i).getCreatedAt().equals(reviews.get(i + 1).getCreatedAt()));
        }

        // Verify no blocked reviews
        for (Review review : reviews) {
            assertFalse(review.isBlocked());
        }
    }

    @Test
    public void test_findReviewsByUserPaginated() {
        // 1. Pre-conditions
        int page = 1;
        int pageSize = 10;

        // 2. Execute
        List<Review> reviews = reviewDao.findReviewsByUserPaginated(PRE_EXISTING_USER_ID, page, pageSize);

        // 3. Post-conditions
        assertNotNull(reviews);
        assertEquals(3, reviews.size()); // User has 3 reviews

        // Verify all reviews belong to the user
        for(Review review : reviews) {
            assertEquals(PRE_EXISTING_USER_ID, review.getUser().getId().longValue());
        }

        // Verify reviews are ordered by creation date (DESC)
        LocalDateTime previousDate = LocalDateTime.now();
        for(Review review : reviews) {
            assertTrue(review.getCreatedAt().isBefore(previousDate) || review.getCreatedAt().equals(previousDate));
            previousDate = review.getCreatedAt();
        }
    }

    @Test
    public void test_findReviewsByUserPaginated_WithOffset() {
        // 1. Pre-conditions
        int limit = 1;
        int offset = 1;

        // 2. Execute
        List<Review> reviews = reviewDao.findReviewsByUserPaginated(PRE_EXISTING_USER_ID, limit, offset);

        // 3. Post-conditions
        assertNotNull(reviews);
        assertEquals(1, reviews.size());

        // Verify correct offset
        assertNotEquals(reviews.getFirst().getId().longValue(), PRE_EXISTING_REVIEW_ID);
    }

    @Test
    public void test_updateCommentAmount() {
        // 1. Pre-conditions
        Review review = em.find(Review.class, PRE_EXISTING_REVIEW_ID);
        int initialCommentAmount = review.getCommentAmount();

        // Add a new comment
        User user = em.find(User.class, PRE_EXISTING_USER_ID);
        Comment newComment = new Comment(user, review, NEW_COMMENT);
        em.persist(newComment);
        em.flush();

        // 2. Execute
        reviewDao.updateCommentAmount(PRE_EXISTING_REVIEW_ID);

        // 3. Post-conditions
        em.flush();
        em.clear();

        Review updatedReview = em.find(Review.class, PRE_EXISTING_REVIEW_ID);
        assertEquals(initialCommentAmount + 1, updatedReview.getCommentAmount().intValue());

        // Verify the count matches actual comments in database
        Long commentCount = em.createQuery("SELECT COUNT(c) FROM Comment c WHERE c.review.id = :reviewId", Long.class)
                .setParameter("reviewId", PRE_EXISTING_REVIEW_ID)
                .getSingleResult();
        assertEquals(commentCount.intValue(), updatedReview.getCommentAmount().intValue());
    }

    @Test
    public void test_updateCommentAmount_AfterCommentDeletion() {
        // 1. Pre-conditions
        Review review = em.find(Review.class, PRE_EXISTING_REVIEW_ID);
        int initialCommentAmount = review.getCommentAmount();

        // Delete a comment
        Comment comment = em.createQuery("SELECT c FROM Comment c WHERE c.review.id = :reviewId", Comment.class)
                .setParameter("reviewId", PRE_EXISTING_REVIEW_ID)
                .setMaxResults(1)
                .getSingleResult();
        em.remove(comment);
        em.flush();

        // 2. Execute
        reviewDao.updateCommentAmount(PRE_EXISTING_REVIEW_ID);

        // 3. Post-conditions
        em.flush();
        em.clear();

        Review updatedReview = em.find(Review.class, PRE_EXISTING_REVIEW_ID);
        assertEquals(initialCommentAmount - 1, updatedReview.getCommentAmount().intValue());

        // Verify the count matches actual comments in database
        Long commentCount = em.createQuery("SELECT COUNT(c) FROM Comment c WHERE c.review.id = :reviewId", Long.class)
                .setParameter("reviewId", PRE_EXISTING_REVIEW_ID)
                .getSingleResult();
        assertEquals(commentCount.intValue(), updatedReview.getCommentAmount().intValue());
    }
}
