package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.persistence.ArtistDao;
import ar.edu.itba.paw.exception.not_found.ArtistNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class ArtistServiceImplTest {

    private static final long ARTIST_ID = 1L;
    private static final long USER_ID = 100L;
    private static final String NAME = "Test Artist";
    private static final String BIO = "Artist Biography";
    private static final long DEFAULT_IMG_ID = 1L;
    private static final long CUSTOM_IMG_ID = 5L;

    @InjectMocks
    private ArtistServiceImpl artistService;

    @Mock
    private ArtistDao artistDao;

    @Mock
    private ImageService imageService;

    @Mock
    private AlbumService albumService;

    @Mock
    private UserService userService;

    private Artist testArtist;
    private Image defaultImage;
    private Image customImage;
    private Album testAlbum;

    @Before
    public void setUp() {
        defaultImage = new Image(DEFAULT_IMG_ID, new byte[]{});
        customImage = new Image(CUSTOM_IMG_ID, new byte[]{1, 2, 3});
        testArtist = new Artist(ARTIST_ID, NAME, BIO, defaultImage);
        testAlbum = new Album(1L);
    }

    // ========== CREATE TESTS ==========

    @Test
    public void test_create_withDefaultImage() {
        // 1. Pre-conditions
        Artist artistInput = new Artist(NAME, BIO, null);

        Mockito.when(imageService.getDefaultImgId()).thenReturn(DEFAULT_IMG_ID);
        Mockito.when(imageService.findById(DEFAULT_IMG_ID)).thenReturn(defaultImage);
        Mockito.when(artistDao.create(any(Artist.class))).thenReturn(testArtist);

        // 2. Execute
        Artist created = artistService.create(artistInput);

        // 3. Post-conditions
        assertNotNull(created);
        assertEquals(NAME, created.getName());
        assertEquals(BIO, created.getBio());
        assertEquals(defaultImage, created.getImage());

        Mockito.verify(imageService).getDefaultImgId();
        Mockito.verify(artistDao).create(any(Artist.class));
    }

    @Test
    public void test_create_withCustomImage() {
        // 1. Pre-conditions
        Artist artistInput = new Artist(NAME, BIO, customImage);

        Mockito.when(imageService.findById(CUSTOM_IMG_ID)).thenReturn(customImage);
        Mockito.when(artistDao.create(any(Artist.class))).thenReturn(testArtist);

        // 2. Execute
        Artist created = artistService.create(artistInput);

        // 3. Post-conditions
        assertNotNull(created);
        Mockito.verify(imageService).findById(CUSTOM_IMG_ID);
        Mockito.verify(imageService, Mockito.never()).getDefaultImgId();
        Mockito.verify(artistDao).create(any(Artist.class));
    }

    @Test
    public void test_create_withAlbums() {
        // 1. Pre-conditions
        List<Album> albums = Arrays.asList(testAlbum);
        Artist artistInput = new Artist(NAME, BIO, null);
        artistInput.setAlbums(albums);

        Mockito.when(imageService.getDefaultImgId()).thenReturn(DEFAULT_IMG_ID);
        Mockito.when(imageService.findById(DEFAULT_IMG_ID)).thenReturn(defaultImage);
        Mockito.when(artistDao.create(any(Artist.class))).thenReturn(testArtist);

        // 2. Execute
        Artist created = artistService.create(artistInput);

        // 3. Post-conditions
        assertNotNull(created);
        Mockito.verify(albumService).createAll(albums, testArtist);
    }

    @Test
    public void test_create_withoutAlbums() {
        // 1. Pre-conditions
        Artist artistInput = new Artist(NAME, BIO, null);

        Mockito.when(imageService.getDefaultImgId()).thenReturn(DEFAULT_IMG_ID);
        Mockito.when(imageService.findById(DEFAULT_IMG_ID)).thenReturn(defaultImage);
        Mockito.when(artistDao.create(any(Artist.class))).thenReturn(testArtist);

        // 2. Execute
        Artist created = artistService.create(artistInput);

        // 3. Post-conditions
        assertNotNull(created);
        Mockito.verify(albumService, Mockito.never()).createAll(anyList(), any(Artist.class));
    }

    // ========== UPDATE TESTS ==========

    @Test
    public void test_update_successful() {
        // 1. Pre-conditions
        Artist artistUpdate = new Artist();
        artistUpdate.setId(ARTIST_ID);
        artistUpdate.setName("Updated Name");
        artistUpdate.setBio("Updated Bio");

        Mockito.when(artistDao.findById(ARTIST_ID)).thenReturn(Optional.of(testArtist));
        Mockito.when(artistDao.update(any(Artist.class))).thenReturn(testArtist);

        // 2. Execute
        Artist updated = artistService.update(artistUpdate);

        // 3. Post-conditions
        assertNotNull(updated);
        Mockito.verify(artistDao).update(any(Artist.class));
    }

    @Test(expected = ArtistNotFoundException.class)
    public void test_update_artistNotFound() {
        // 1. Pre-conditions
        Artist artistUpdate = new Artist();
        artistUpdate.setId(ARTIST_ID);

        Mockito.when(artistDao.findById(ARTIST_ID)).thenReturn(Optional.empty());

        // 2. Execute
        artistService.update(artistUpdate);

        // 3. Post-conditions - exception thrown
    }

    @Test
    public void test_update_withNewImage() {
        // 1. Pre-conditions
        Artist artistUpdate = new Artist();
        artistUpdate.setId(ARTIST_ID);
        artistUpdate.setImage(customImage);

        Mockito.when(artistDao.findById(ARTIST_ID)).thenReturn(Optional.of(testArtist));
        Mockito.when(imageService.findById(CUSTOM_IMG_ID)).thenReturn(customImage);
        Mockito.when(artistDao.update(any(Artist.class))).thenReturn(testArtist);

        // 2. Execute
        artistService.update(artistUpdate);

        // 3. Post-conditions
        Mockito.verify(imageService).findById(CUSTOM_IMG_ID);
        assertEquals(customImage, testArtist.getImage());
    }

    @Test
    public void test_update_withAlbums() {
        // 1. Pre-conditions
        List<Album> albums = Arrays.asList(testAlbum);
        Artist artistUpdate = new Artist();
        artistUpdate.setId(ARTIST_ID);
        artistUpdate.setAlbums(albums);

        Mockito.when(artistDao.findById(ARTIST_ID)).thenReturn(Optional.of(testArtist));
        Mockito.when(artistDao.update(any(Artist.class))).thenReturn(testArtist);

        // 2. Execute
        artistService.update(artistUpdate);

        // 3. Post-conditions
        Mockito.verify(albumService).updateAll(albums, testArtist);
    }

    // ========== DELETE TESTS ==========

    @Test
    public void test_delete_successful() {
        // 1. Pre-conditions
        List<Album> albums = Arrays.asList(testAlbum);

        Mockito.when(artistDao.findById(ARTIST_ID)).thenReturn(Optional.of(testArtist));
        Mockito.when(albumService.findByArtistId(ARTIST_ID)).thenReturn(albums);
        Mockito.when(artistDao.findReviewsByArtistId(ARTIST_ID)).thenReturn(new ArrayList<>());
        Mockito.when(artistDao.delete(ARTIST_ID)).thenReturn(true);

        // 2. Execute
        Boolean result = artistService.delete(ARTIST_ID);

        // 3. Post-conditions
        assertTrue(result);
        Mockito.verify(albumService).delete(testAlbum.getId());
        Mockito.verify(artistDao).deleteReviewsFromArtist(ARTIST_ID);
        Mockito.verify(artistDao).delete(ARTIST_ID);
        Mockito.verify(imageService).delete(defaultImage.getId());
    }

    @Test(expected = ArtistNotFoundException.class)
    public void test_delete_artistNotFound() {
        // 1. Pre-conditions
        Mockito.when(artistDao.findById(ARTIST_ID)).thenReturn(Optional.empty());

        // 2. Execute
        artistService.delete(ARTIST_ID);

        // 3. Post-conditions - exception thrown
    }

    @Test
    public void test_delete_cascadeAlbums() {
        // 1. Pre-conditions
        Album album1 = new Album(1L);
        Album album2 = new Album(2L);
        List<Album> albums = Arrays.asList(album1, album2);

        Mockito.when(artistDao.findById(ARTIST_ID)).thenReturn(Optional.of(testArtist));
        Mockito.when(albumService.findByArtistId(ARTIST_ID)).thenReturn(albums);
        Mockito.when(artistDao.findReviewsByArtistId(ARTIST_ID)).thenReturn(new ArrayList<>());
        Mockito.when(artistDao.delete(ARTIST_ID)).thenReturn(true);

        // 2. Execute
        artistService.delete(ARTIST_ID);

        // 3. Post-conditions - verify all albums deleted
        Mockito.verify(albumService).delete(1L);
        Mockito.verify(albumService).delete(2L);
    }

    @Test
    public void test_delete_updatesUserReviewAmounts() {
        // 1. Pre-conditions
        User user1 = new User(1L, "user1", "user1@test.com", "pass");
        User user2 = new User(2L, "user2", "user2@test.com", "pass");

        ArtistReview review1 = new ArtistReview();
        review1.setUser(user1);
        ArtistReview review2 = new ArtistReview();
        review2.setUser(user2);
        List<ArtistReview> reviews = Arrays.asList(review1, review2);

        Mockito.when(artistDao.findById(ARTIST_ID)).thenReturn(Optional.of(testArtist));
        Mockito.when(albumService.findByArtistId(ARTIST_ID)).thenReturn(new ArrayList<>());
        Mockito.when(artistDao.findReviewsByArtistId(ARTIST_ID)).thenReturn(reviews);
        Mockito.when(artistDao.delete(ARTIST_ID)).thenReturn(true);

        // 2. Execute
        artistService.delete(ARTIST_ID);

        // 3. Post-conditions - verify user review amounts updated
        Mockito.verify(userService).updateUserReviewAmount(1L);
        Mockito.verify(userService).updateUserReviewAmount(2L);
    }

    // ========== FIND TESTS ==========

    @Test
    public void test_findById_successful() {
        // 1. Pre-conditions
        Mockito.when(artistDao.findById(ARTIST_ID)).thenReturn(Optional.of(testArtist));

        // 2. Execute
        Artist found = artistService.findById(ARTIST_ID);

        // 3. Post-conditions
        assertNotNull(found);
        assertEquals(ARTIST_ID, found.getId().longValue());
        assertEquals(NAME, found.getName());
    }

    @Test(expected = ArtistNotFoundException.class)
    public void test_findById_notFound() {
        // 1. Pre-conditions
        Mockito.when(artistDao.findById(ARTIST_ID)).thenReturn(Optional.empty());

        // 2. Execute
        artistService.findById(ARTIST_ID);

        // 3. Post-conditions - exception thrown
    }

    @Test
    public void test_findAll_successful() {
        // 1. Pre-conditions
        List<Artist> artists = Arrays.asList(testArtist);
        Mockito.when(artistDao.findAll()).thenReturn(artists);

        // 2. Execute
        List<Artist> result = artistService.findAll();

        // 3. Post-conditions
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testArtist, result.get(0));
    }

    @Test
    public void test_findPaginated_successful() {
        // 1. Pre-conditions
        List<Artist> artists = Arrays.asList(testArtist);
        Mockito.when(artistDao.findPaginated(FilterType.RECENT, 10, 0)).thenReturn(artists);

        // 2. Execute
        List<Artist> result = artistService.findPaginated(FilterType.RECENT, 1, 10);

        // 3. Post-conditions
        assertNotNull(result);
        assertEquals(1, result.size());
        Mockito.verify(artistDao).findPaginated(FilterType.RECENT, 10, 0);
    }

    @Test
    public void test_findByNameContaining_successful() {
        // 1. Pre-conditions
        List<Artist> artists = Arrays.asList(testArtist);
        Mockito.when(artistDao.findByNameContaining("Test", 1, 10)).thenReturn(artists);

        // 2. Execute
        List<Artist> result = artistService.findByNameContaining("Test", 1, 10);

        // 3. Post-conditions
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void test_findBySongId_successful() {
        // 1. Pre-conditions
        List<Artist> artists = Arrays.asList(testArtist);
        Mockito.when(artistDao.findBySongId(100L)).thenReturn(artists);

        // 2. Execute
        List<Artist> result = artistService.findBySongId(100L);

        // 3. Post-conditions
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ========== RATING TESTS ==========

    @Test
    public void test_updateRating_successful() {
        // 1. Pre-conditions
        ArtistReview review1 = new ArtistReview();
        review1.setRating(5);
        ArtistReview review2 = new ArtistReview();
        review2.setRating(4);
        List<ArtistReview> reviews = Arrays.asList(review1, review2);

        Mockito.when(artistDao.findReviewsByArtistId(ARTIST_ID)).thenReturn(reviews);
        Mockito.when(artistDao.updateRating(eq(ARTIST_ID), anyDouble(), anyInt())).thenReturn(true);

        // 2. Execute
        Boolean result = artistService.updateRating(ARTIST_ID);

        // 3. Post-conditions
        assertTrue(result);
        // Average of 5 and 4 is 4.5
        Mockito.verify(artistDao).updateRating(ARTIST_ID, 4.5, 2);
    }

    @Test
    public void test_updateRating_noReviews() {
        // 1. Pre-conditions
        Mockito.when(artistDao.findReviewsByArtistId(ARTIST_ID)).thenReturn(new ArrayList<>());
        Mockito.when(artistDao.updateRating(eq(ARTIST_ID), anyDouble(), anyInt())).thenReturn(true);

        // 2. Execute
        Boolean result = artistService.updateRating(ARTIST_ID);

        // 3. Post-conditions
        assertTrue(result);
        Mockito.verify(artistDao).updateRating(ARTIST_ID, 0.0, 0);
    }

    @Test
    public void test_updateRating_rounding() {
        // 1. Pre-conditions
        ArtistReview review1 = new ArtistReview();
        review1.setRating(5);
        ArtistReview review2 = new ArtistReview();
        review2.setRating(4);
        ArtistReview review3 = new ArtistReview();
        review3.setRating(3);
        List<ArtistReview> reviews = Arrays.asList(review1, review2, review3);

        Mockito.when(artistDao.findReviewsByArtistId(ARTIST_ID)).thenReturn(reviews);
        Mockito.when(artistDao.updateRating(eq(ARTIST_ID), anyDouble(), anyInt())).thenReturn(true);

        // 2. Execute
        artistService.updateRating(ARTIST_ID);

        // 3. Post-conditions
        // Average of 5, 4, 3 is 4.0
        Mockito.verify(artistDao).updateRating(ARTIST_ID, 4.0, 3);
    }

    // ========== REVIEW TESTS ==========

    @Test
    public void test_findReviewsByArtistId_successful() {
        // 1. Pre-conditions
        ArtistReview review = new ArtistReview();
        List<ArtistReview> reviews = Arrays.asList(review);

        Mockito.when(artistDao.findReviewsByArtistId(ARTIST_ID)).thenReturn(reviews);

        // 2. Execute
        List<Review> result = artistService.findReviewsByArtistId(ARTIST_ID);

        // 3. Post-conditions
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void test_hasUserReviewed_returnsTrue() {
        // 1. Pre-conditions
        Mockito.when(artistDao.hasUserReviewed(USER_ID, ARTIST_ID)).thenReturn(true);

        // 2. Execute
        Boolean result = artistService.hasUserReviewed(USER_ID, ARTIST_ID);

        // 3. Post-conditions
        assertTrue(result);
    }

    @Test
    public void test_hasUserReviewed_returnsFalse() {
        // 1. Pre-conditions
        Mockito.when(artistDao.hasUserReviewed(USER_ID, ARTIST_ID)).thenReturn(false);

        // 2. Execute
        Boolean result = artistService.hasUserReviewed(USER_ID, ARTIST_ID);

        // 3. Post-conditions
        assertFalse(result);
    }

    // ========== COUNT TESTS ==========

    @Test
    public void test_countAll_successful() {
        // 1. Pre-conditions
        Mockito.when(artistDao.countAll()).thenReturn(50L);

        // 2. Execute
        Long count = artistService.countAll();

        // 3. Post-conditions
        assertEquals(Long.valueOf(50), count);
    }

    // ========== CONTEXT DEPENDENT FIELDS TESTS ==========

    @Test
    public void test_setContextDependentFields_withLoggedUser() {
        // 1. Pre-conditions
        Mockito.when(artistDao.hasUserReviewed(USER_ID, ARTIST_ID)).thenReturn(true);
        Mockito.when(userService.isArtistFavorite(USER_ID, ARTIST_ID)).thenReturn(true);

        // 2. Execute
        artistService.setContextDependentFields(testArtist, USER_ID);

        // 3. Post-conditions
        assertTrue(testArtist.getIsReviewed());
        assertTrue(testArtist.getIsFavorite());
    }

    @Test
    public void test_setContextDependentFields_withoutLoggedUser() {
        // 1. Pre-conditions - none

        // 2. Execute
        artistService.setContextDependentFields(testArtist, null);

        // 3. Post-conditions
        assertFalse(testArtist.getIsReviewed());
        assertFalse(testArtist.getIsFavorite());
        Mockito.verify(artistDao, Mockito.never()).hasUserReviewed(anyLong(), anyLong());
        Mockito.verify(userService, Mockito.never()).isArtistFavorite(anyLong(), anyLong());
    }

    @Test
    public void test_findAndSetContextDependentFields_successful() {
        // 1. Pre-conditions
        Mockito.when(artistDao.findById(ARTIST_ID)).thenReturn(Optional.of(testArtist));
        Mockito.when(artistDao.hasUserReviewed(USER_ID, ARTIST_ID)).thenReturn(false);
        Mockito.when(userService.isArtistFavorite(USER_ID, ARTIST_ID)).thenReturn(false);

        // 2. Execute
        Artist result = artistService.findAndSetContextDependentFields(ARTIST_ID, USER_ID);

        // 3. Post-conditions
        assertNotNull(result);
        assertFalse(result.getIsReviewed());
        assertFalse(result.getIsFavorite());
    }
}
