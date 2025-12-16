package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.persistence.SongDao;
import ar.edu.itba.paw.persistence.AlbumDao;
import ar.edu.itba.paw.exception.not_found.SongNotFoundException;
import ar.edu.itba.paw.exception.not_found.AlbumNotFoundException;
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
public class SongServiceImplTest {

    private static final long SONG_ID = 1L;
    private static final long ALBUM_ID = 10L;
    private static final long ARTIST_ID = 20L;
    private static final long USER_ID = 100L;
    private static final String TITLE = "Test Song";
    private static final String DURATION = "3:00";

    @InjectMocks
    private SongServiceImpl songService;

    @Mock
    private SongDao songDao;

    @Mock
    private AlbumDao albumDao;

    @Mock
    private UserService userService;

    private Song testSong;
    private Album testAlbum;
    private Artist testArtist;

    @Before
    public void setUp() {
        testArtist = new Artist(ARTIST_ID, "Test Artist", "Bio", null);
        testAlbum = new Album(ALBUM_ID);
        testAlbum.setTitle("Test Album");
        testAlbum.setArtist(testArtist);
        testSong = new Song(SONG_ID, TITLE, DURATION, testAlbum);
        testSong.setAlbum(testAlbum);
    }

    // ========== CREATE TESTS ==========

    @Test
    public void test_create_withAlbum() {
        // 1. Pre-conditions
        Song songInput = new Song(TITLE, DURATION, 1, testAlbum);

        Mockito.when(albumDao.findById(ALBUM_ID)).thenReturn(Optional.of(testAlbum));
        Mockito.when(songDao.create(any(Song.class))).thenReturn(testSong);

        // 2. Execute
        Song created = songService.create(songInput);

        // 3. Post-conditions
        assertNotNull(created);
        Mockito.verify(songDao).create(any(Song.class));
        Mockito.verify(songDao).saveSongArtist(any(Song.class), eq(testArtist));
    }

    @Test
    public void test_create_withoutAlbum() {
        // 1. Pre-conditions
        Song songInput = new Song(TITLE, DURATION, 1, null);
        songInput.setArtists(Arrays.asList(testArtist));

        Mockito.when(songDao.create(any(Song.class))).thenReturn(testSong);

        // 2. Execute
        Song created = songService.create(songInput);

        // 3. Post-conditions
        assertNotNull(created);
        Mockito.verify(songDao).create(any(Song.class));
        Mockito.verify(songDao).saveSongArtist(any(Song.class), eq(testArtist));
    }

    @Test(expected = AlbumNotFoundException.class)
    public void test_create_albumNotFound() {
        // 1. Pre-conditions
        Song songInput = new Song(TITLE, DURATION, 1, testAlbum);

        Mockito.when(albumDao.findById(ALBUM_ID)).thenReturn(Optional.empty());

        // 2. Execute
        songService.create(songInput);

        // 3. Post-conditions - exception thrown
    }

    @Test
    public void test_createAll_successful() {
        // 1. Pre-conditions
        List<Song> songs = Arrays.asList(testSong);

        Mockito.when(albumDao.findById(ALBUM_ID)).thenReturn(Optional.of(testAlbum));
        Mockito.when(songDao.create(any(Song.class))).thenReturn(testSong);

        // 2. Execute
        Boolean result = songService.createAll(songs, testAlbum);

        // 3. Post-conditions
        assertTrue(result);
        Mockito.verify(songDao).create(any(Song.class));
        Mockito.verify(songDao).saveSongArtist(any(Song.class), eq(testArtist));
    }

    // ========== UPDATE TESTS ==========

    @Test
    public void test_update_successful() {
        // 1. Pre-conditions
        Song songUpdate = new Song();
        songUpdate.setId(SONG_ID);
        songUpdate.setTitle("Updated Title");

        Mockito.when(songDao.findById(SONG_ID)).thenReturn(Optional.of(testSong));
        Mockito.when(songDao.update(any(Song.class))).thenReturn(testSong);

        // 2. Execute
        Song updated = songService.update(songUpdate);

        // 3. Post-conditions
        assertNotNull(updated);
        Mockito.verify(songDao).update(any(Song.class));
    }

    @Test(expected = SongNotFoundException.class)
    public void test_update_songNotFound() {
        // 1. Pre-conditions
        Song songUpdate = new Song();
        songUpdate.setId(SONG_ID);

        Mockito.when(songDao.findById(SONG_ID)).thenReturn(Optional.empty());

        // 2. Execute
        songService.update(songUpdate);

        // 3. Post-conditions - exception thrown
    }

    // ========== DELETE TESTS ==========

    @Test
    public void test_delete_successful() {
        // 1. Pre-conditions
        Mockito.when(songDao.findReviewsBySongId(SONG_ID)).thenReturn(new ArrayList<>());
        Mockito.when(songDao.delete(SONG_ID)).thenReturn(true);

        // 2. Execute
        Boolean result = songService.delete(SONG_ID);

        // 3. Post-conditions
        assertTrue(result);
        Mockito.verify(songDao).deleteReviewsFromSong(SONG_ID);
        Mockito.verify(songDao).delete(SONG_ID);
    }

    @Test
    public void test_delete_updatesUserReviewAmounts() {
        // 1. Pre-conditions
        User user1 = new User(1L, "user1", "user1@test.com", "pass");
        User user2 = new User(2L, "user2", "user2@test.com", "pass");

        SongReview review1 = new SongReview();
        review1.setUser(user1);
        SongReview review2 = new SongReview();
        review2.setUser(user2);
        List<SongReview> reviews = Arrays.asList(review1, review2);

        Mockito.when(songDao.findReviewsBySongId(SONG_ID)).thenReturn(reviews);
        Mockito.when(songDao.delete(SONG_ID)).thenReturn(true);

        // 2. Execute
        songService.delete(SONG_ID);

        // 3. Post-conditions
        Mockito.verify(userService).updateUserReviewAmount(1L);
        Mockito.verify(userService).updateUserReviewAmount(2L);
    }

    // ========== FIND TESTS ==========

    @Test
    public void test_findById_successful() {
        // 1. Pre-conditions
        Mockito.when(songDao.findById(SONG_ID)).thenReturn(Optional.of(testSong));

        // 2. Execute
        Song found = songService.findById(SONG_ID);

        // 3. Post-conditions
        assertNotNull(found);
        assertEquals(SONG_ID, found.getId().longValue());
        assertEquals(TITLE, found.getTitle());
    }

    @Test(expected = SongNotFoundException.class)
    public void test_findById_notFound() {
        // 1. Pre-conditions
        Mockito.when(songDao.findById(SONG_ID)).thenReturn(Optional.empty());

        // 2. Execute
        songService.findById(SONG_ID);

        // 3. Post-conditions - exception thrown
    }

    // ========== RATING TESTS ==========

    @Test
    public void test_updateRating_successful() {
        // 1. Pre-conditions
        SongReview review1 = new SongReview();
        review1.setRating(5);
        SongReview review2 = new SongReview();
        review2.setRating(4);
        List<SongReview> reviews = new ArrayList<>();
        reviews.add(review1);
        reviews.add(review2);

        Mockito.when(songDao.findReviewsBySongId(SONG_ID)).thenReturn(reviews);
        Mockito.when(songDao.updateRating(eq(SONG_ID), anyDouble(), anyInt())).thenReturn(true);

        // 2. Execute
        Boolean result = songService.updateRating(SONG_ID);

        // 3. Post-conditions
        assertTrue(result);
        Mockito.verify(songDao).updateRating(SONG_ID, 4.5, 2);
    }

    @Test
    public void test_hasUserReviewed_returnsTrue() {
        // 1. Pre-conditions
        Mockito.when(songDao.hasUserReviewed(USER_ID, SONG_ID)).thenReturn(true);

        // 2. Execute
        Boolean result = songService.hasUserReviewed(USER_ID, SONG_ID);

        // 3. Post-conditions
        assertTrue(result);
    }

    // ========== CONTEXT DEPENDENT FIELDS TESTS ==========

    @Test
    public void test_setContextDependentFields_withLoggedUser() {
        // 1. Pre-conditions
        Mockito.when(songDao.hasUserReviewed(USER_ID, SONG_ID)).thenReturn(true);
        Mockito.when(userService.isSongFavorite(USER_ID, SONG_ID)).thenReturn(true);

        // 2. Execute
        songService.setContextDependentFields(testSong, USER_ID);

        // 3. Post-conditions
        assertTrue(testSong.getIsReviewed());
        assertTrue(testSong.getIsFavorite());
    }

    @Test
    public void test_setContextDependentFields_withoutLoggedUser() {
        // 1. Pre-conditions - none

        // 2. Execute
        songService.setContextDependentFields(testSong, null);

        // 3. Post-conditions
        assertFalse(testSong.getIsReviewed());
        assertFalse(testSong.getIsFavorite());
        Mockito.verify(songDao, Mockito.never()).hasUserReviewed(anyLong(), anyLong());
        Mockito.verify(userService, Mockito.never()).isSongFavorite(anyLong(), anyLong());
    }
}
