package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.models.reviews.ReviewType;
import ar.edu.itba.paw.persistence.*;
import ar.edu.itba.paw.exception.email.AcknowledgementEmailException;
import ar.edu.itba.paw.exception.conflict.UserAlreadyReviewedException;
import ar.edu.itba.paw.exception.not_found.ReviewNotFoundException;
import ar.edu.itba.paw.exception.not_found.UserNotFoundException;
import ar.edu.itba.paw.exception.not_found.ArtistNotFoundException;
import ar.edu.itba.paw.exception.not_found.AlbumNotFoundException;
import ar.edu.itba.paw.exception.not_found.SongNotFoundException;
import ar.edu.itba.paw.exception.conflict.LikeAlreadyExistsException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.mail.MessagingException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class ReviewServiceImplTest {

    private static final Long REVIEW_ID = 1L;
    private static final Long USER_ID = 100L;
    private static final Long OTHER_USER_ID = 200L;
    private static final Long ARTIST_ID = 10L;
    private static final Long ALBUM_ID = 20L;
    private static final Long SONG_ID = 30L;
    private static final String REVIEW_TITLE = "Great Artist";
    private static final String REVIEW_DESCRIPTION = "Amazing music!";
    private static final Integer REVIEW_RATING = 5;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Mock
    private ReviewDao reviewDao;

    @Mock
    private UserDao userDao;

    @Mock
    private ArtistDao artistDao;

    @Mock
    private AlbumDao albumDao;

    @Mock
    private SongDao songDao;

    @Mock
    private SongService songService;

    @Mock
    private ArtistService artistService;

    @Mock
    private AlbumService albumService;

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationService notificationService;

    private User testUser;
    private Artist testArtist;
    private Album testAlbum;
    private Song testSong;
    private ArtistReview artistReview;
    private AlbumReview albumReview;
    private SongReview songReview;

    @Before
    public void setUp() {
        testUser = new User(USER_ID, "testuser", "test@test.com", "password");
        testArtist = new Artist(ARTIST_ID, "Test Artist", "Bio", null);
        testAlbum = new Album(ALBUM_ID);
        testSong = new Song(SONG_ID);

        artistReview = new ArtistReview();
        artistReview.setId(REVIEW_ID);
        artistReview.setUser(testUser);
        artistReview.setArtist(testArtist);
        artistReview.setTitle(REVIEW_TITLE);
        artistReview.setDescription(REVIEW_DESCRIPTION);
        artistReview.setRating(REVIEW_RATING);
        artistReview.setBlocked(false);
        artistReview.setLikes(0);

        albumReview = new AlbumReview();
        albumReview.setId(REVIEW_ID);
        albumReview.setUser(testUser);
        albumReview.setAlbum(testAlbum);
        albumReview.setTitle(REVIEW_TITLE);
        albumReview.setRating(REVIEW_RATING);

        songReview = new SongReview();
        songReview.setId(REVIEW_ID);
        songReview.setUser(testUser);
        songReview.setSong(testSong);
        songReview.setTitle(REVIEW_TITLE);
        songReview.setRating(REVIEW_RATING);
    }

    // ========== CREATE ARTIST REVIEW TESTS ==========

    @Test
    public void test_createArtistReview_successful() {
        // 1. Pre-conditions
        ArtistReview reviewInput = new ArtistReview();
        User inputUser = new User(USER_ID, "testuser", "test@test.com", "password");
        reviewInput.setUser(inputUser);
        reviewInput.setArtist(testArtist);
        reviewInput.setTitle(REVIEW_TITLE);
        reviewInput.setDescription(REVIEW_DESCRIPTION);
        reviewInput.setRating(REVIEW_RATING);

        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(testUser));
        Mockito.when(artistDao.findById(ARTIST_ID)).thenReturn(Optional.of(testArtist));
        Mockito.when(artistService.hasUserReviewed(USER_ID, ARTIST_ID)).thenReturn(false);
        Mockito.when(reviewDao.create(any(ArtistReview.class))).thenReturn(artistReview);
        Mockito.when(artistService.findReviewsByArtistId(ARTIST_ID)).thenReturn(Arrays.asList(artistReview));

        // 2. Execute
        Review created = reviewService.create(reviewInput);

        // 3. Post-conditions
        assertNotNull(created);
        Mockito.verify(reviewDao).create(any(ArtistReview.class));
        Mockito.verify(userService).updateUserReviewAmount(USER_ID);
        Mockito.verify(notificationService).notifyNewReview(any(Review.class));
        Mockito.verify(artistDao).updateRating(eq(ARTIST_ID), anyDouble(), anyInt());
    }

    @Test(expected = UserAlreadyReviewedException.class)
    public void test_createArtistReview_userAlreadyReviewed() {
        // 1. Pre-conditions
        ArtistReview reviewInput = new ArtistReview();
        User inputUser = new User(USER_ID, "testuser", "test@test.com", "password");
        reviewInput.setUser(inputUser);
        reviewInput.setArtist(testArtist);

        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(testUser));
        Mockito.when(artistDao.findById(ARTIST_ID)).thenReturn(Optional.of(testArtist));
        Mockito.when(artistService.hasUserReviewed(USER_ID, ARTIST_ID)).thenReturn(true);

        // 2. Execute
        reviewService.create(reviewInput);

        // 3. Post-conditions - exception thrown
    }

    @Test(expected = UserNotFoundException.class)
    public void test_createArtistReview_userNotFound() {
        // 1. Pre-conditions
        ArtistReview reviewInput = new ArtistReview();
        User inputUser = new User(USER_ID, "testuser", "test@test.com", "password");
        reviewInput.setUser(inputUser);
        reviewInput.setArtist(testArtist);

        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        // 2. Execute
        reviewService.create(reviewInput);

        // 3. Post-conditions - exception thrown
    }

    // ========== FIND TESTS ==========

    @Test
    public void test_findById_successful() {
        // 1. Pre-conditions
        Mockito.when(reviewDao.findById(REVIEW_ID)).thenReturn(Optional.of(artistReview));

        // 2. Execute
        Review found = reviewService.findById(REVIEW_ID);

        // 3. Post-conditions
        assertNotNull(found);
        assertEquals(REVIEW_ID, found.getId());
    }

    @Test(expected = ReviewNotFoundException.class)
    public void test_findById_notFound() {
        // 1. Pre-conditions
        Mockito.when(reviewDao.findById(REVIEW_ID)).thenReturn(Optional.empty());

        // 2. Execute
        reviewService.findById(REVIEW_ID);

        // 3. Post-conditions - exception thrown
    }

    // ========== DELETE TESTS ==========

    @Test
    public void test_delete_successful() {
        // 1. Pre-conditions
        Mockito.when(reviewDao.findById(REVIEW_ID)).thenReturn(Optional.of(artistReview));
        Mockito.when(reviewDao.delete(REVIEW_ID)).thenReturn(true);
        Mockito.when(artistService.findReviewsByArtistId(ARTIST_ID)).thenReturn(Arrays.asList());

        // 2. Execute
        Boolean result = reviewService.delete(REVIEW_ID);

        // 3. Post-conditions
        assertTrue(result);
        Mockito.verify(reviewDao).delete(REVIEW_ID);
        Mockito.verify(userService).updateUserReviewAmount(USER_ID);
        Mockito.verify(artistDao).updateRating(eq(ARTIST_ID), anyDouble(), anyInt());
    }

    // ========== LIKE TESTS ==========

    @Test
    public void test_createLike_successful() {
        // 1. Pre-conditions
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(testUser));
        Mockito.when(reviewDao.findById(REVIEW_ID)).thenReturn(Optional.of(artistReview));
        Mockito.when(reviewDao.isLiked(USER_ID, REVIEW_ID)).thenReturn(false);

        // 2. Execute
        reviewService.createLike(USER_ID, REVIEW_ID);

        // 3. Post-conditions
        Mockito.verify(reviewDao).createLike(USER_ID, REVIEW_ID);
        Mockito.verify(reviewDao).updateLikeCount(REVIEW_ID);
        Mockito.verify(notificationService).notifyLike(artistReview, testUser);
    }

    @Test(expected = LikeAlreadyExistsException.class)
    public void test_createLike_alreadyLiked() {
        // 1. Pre-conditions
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(testUser));
        Mockito.when(reviewDao.findById(REVIEW_ID)).thenReturn(Optional.of(artistReview));
        Mockito.when(reviewDao.isLiked(USER_ID, REVIEW_ID)).thenReturn(true);

        // 2. Execute
        reviewService.createLike(USER_ID, REVIEW_ID);

        // 3. Post-conditions - exception thrown
    }

    @Test
    public void test_removeLike_successful() {
        // 1. Pre-conditions - none

        // 2. Execute
        reviewService.removeLike(USER_ID, REVIEW_ID);

        // 3. Post-conditions
        Mockito.verify(reviewDao).deleteLike(USER_ID, REVIEW_ID);
        Mockito.verify(reviewDao).updateLikeCount(REVIEW_ID);
    }

    // ========== BLOCK/UNBLOCK TESTS ==========

    @Test
    public void test_block_successful() throws MessagingException {
        // 1. Pre-conditions
        Mockito.when(reviewDao.findById(REVIEW_ID)).thenReturn(Optional.of(artistReview));
        Mockito.doNothing().when(emailService).sendReviewAcknowledgement(
                any(),
                any(User.class),
                anyString(),
                anyString(),
                anyString()
        );

        // 2. Execute
        reviewService.block(REVIEW_ID);

        // 3. Post-conditions
        Mockito.verify(reviewDao).block(REVIEW_ID);
        Mockito.verify(emailService).sendReviewAcknowledgement(
                eq(ReviewAcknowledgementType.BLOCKED),
                eq(testUser),
                eq(REVIEW_TITLE),
                anyString(),
                anyString()
        );
    }

    @Test
    public void test_unblock_successful() throws MessagingException {
        // 1. Pre-conditions
        Mockito.when(reviewDao.findById(REVIEW_ID)).thenReturn(Optional.of(artistReview));
        Mockito.doNothing().when(emailService).sendReviewAcknowledgement(
                any(),
                any(User.class),
                anyString(),
                anyString(),
                anyString()
        );

        // 2. Execute
        reviewService.unblock(REVIEW_ID);

        // 3. Post-conditions
        Mockito.verify(reviewDao).unblock(REVIEW_ID);
        Mockito.verify(emailService).sendReviewAcknowledgement(
                eq(ReviewAcknowledgementType.UNBLOCKED),
                eq(testUser),
                eq(REVIEW_TITLE),
                anyString(),
                anyString()
        );
    }
}
