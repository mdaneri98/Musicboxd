package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.persistence.AlbumDao;
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
public class AlbumServiceImplTest {

    private static final long ALBUM_ID = 1L;
    private static final long ARTIST_ID = 10L;
    private static final long USER_ID = 100L;
    private static final String TITLE = "Test Album";
    private static final long DEFAULT_IMG_ID = 1L;
    private static final long CUSTOM_IMG_ID = 5L;

    @InjectMocks
    private AlbumServiceImpl albumService;

    @Mock
    private AlbumDao albumDao;

    @Mock
    private ImageService imageService;

    @Mock
    private SongService songService;

    @Mock
    private UserService userService;

    private Album testAlbum;
    private Artist testArtist;
    private Image defaultImage;
    private Image customImage;
    private Song testSong;

    @Before
    public void setUp() {
        defaultImage = new Image(DEFAULT_IMG_ID, new byte[] {});
        customImage = new Image(CUSTOM_IMG_ID, new byte[] { 1, 2, 3 });
        testArtist = new Artist(ARTIST_ID, "Test Artist", "Bio", defaultImage);
        testAlbum = new Album(ALBUM_ID);
        testAlbum.setTitle(TITLE);
        testAlbum.setImage(defaultImage);
        testAlbum.setArtist(testArtist);
        testSong = new Song(1L);
    }

    // ========== CREATE TESTS ==========

    @Test
    public void test_create_withDefaultImage() {
        // 1. Pre-conditions
        Album albumInput = new Album(TITLE, "genre");
        albumInput.setArtist(testArtist);

        Mockito.when(imageService.getDefaultImgId()).thenReturn(DEFAULT_IMG_ID);
        Mockito.when(imageService.findById(DEFAULT_IMG_ID)).thenReturn(defaultImage);
        Mockito.when(albumDao.create(any(Album.class))).thenReturn(testAlbum);

        // 2. Execute
        Album created = albumService.create(albumInput);

        // 3. Post-conditions
        assertNotNull(created);
        assertEquals(TITLE, created.getTitle());
        assertEquals(defaultImage, created.getImage());

        Mockito.verify(imageService).getDefaultImgId();
        Mockito.verify(albumDao).create(any(Album.class));
    }

    @Test
    public void test_create_withCustomImage() {
        // 1. Pre-conditions
        Album albumInput = new Album();
        albumInput.setTitle(TITLE);
        albumInput.setImage(customImage);
        albumInput.setArtist(testArtist);

        Mockito.when(imageService.findById(CUSTOM_IMG_ID)).thenReturn(customImage);
        Mockito.when(albumDao.create(any(Album.class))).thenReturn(testAlbum);

        // 2. Execute
        Album created = albumService.create(albumInput);

        // 3. Post-conditions
        assertNotNull(created);
        Mockito.verify(imageService).findById(CUSTOM_IMG_ID);
        Mockito.verify(imageService, Mockito.never()).getDefaultImgId();
        Mockito.verify(albumDao).create(any(Album.class));
    }

    @Test
    public void test_create_withSongs() {
        // 1. Pre-conditions
        List<Song> songs = Arrays.asList(testSong);
        Album albumInput = new Album(TITLE, "genre");
        albumInput.setArtist(testArtist);
        albumInput.setSongs(songs);

        Mockito.when(imageService.getDefaultImgId()).thenReturn(DEFAULT_IMG_ID);
        Mockito.when(imageService.findById(DEFAULT_IMG_ID)).thenReturn(defaultImage);
        Mockito.when(albumDao.create(any(Album.class))).thenReturn(testAlbum);

        // 2. Execute
        Album created = albumService.create(albumInput);

        // 3. Post-conditions
        assertNotNull(created);
        Mockito.verify(songService).createAll(songs, testAlbum);
    }

    @Test
    public void test_createAll_successful() {
        // 1. Pre-conditions
        List<Album> albums = Arrays.asList(testAlbum);

        Mockito.when(imageService.findById(DEFAULT_IMG_ID)).thenReturn(defaultImage);
        Mockito.when(albumDao.create(any(Album.class))).thenReturn(testAlbum);

        // 2. Execute
        Boolean result = albumService.createAll(albums, testArtist);

        // 3. Post-conditions
        assertTrue(result);
        Mockito.verify(albumDao).create(any(Album.class));
    }

    // ========== UPDATE TESTS ==========

    @Test
    public void test_update_successful() {
        // 1. Pre-conditions
        Album albumUpdate = new Album();
        albumUpdate.setId(ALBUM_ID);
        albumUpdate.setTitle("Updated Title");

        Mockito.when(albumDao.findById(ALBUM_ID)).thenReturn(Optional.of(testAlbum));
        Mockito.when(albumDao.update(any(Album.class))).thenReturn(testAlbum);

        // 2. Execute
        Album updated = albumService.update(albumUpdate);

        // 3. Post-conditions
        assertNotNull(updated);
        Mockito.verify(albumDao).update(any(Album.class));
    }

    @Test(expected = AlbumNotFoundException.class)
    public void test_update_albumNotFound() {
        // 1. Pre-conditions
        Album albumUpdate = new Album();
        albumUpdate.setId(ALBUM_ID);

        Mockito.when(albumDao.findById(ALBUM_ID)).thenReturn(Optional.empty());

        // 2. Execute
        albumService.update(albumUpdate);

        // 3. Post-conditions - exception thrown
    }

    // ========== DELETE TESTS ==========

    @Test
    public void test_delete_successful() {
        // 1. Pre-conditions
        List<Song> songs = Arrays.asList(testSong);
        testAlbum.setSongs(songs);

        Mockito.when(albumDao.findById(ALBUM_ID)).thenReturn(Optional.of(testAlbum));
        Mockito.when(albumDao.findReviewsByAlbumId(ALBUM_ID)).thenReturn(new ArrayList<>());
        Mockito.when(albumDao.delete(ALBUM_ID)).thenReturn(true);

        // 2. Execute
        Boolean result = albumService.delete(ALBUM_ID);

        // 3. Post-conditions
        assertTrue(result);
        Mockito.verify(songService).delete(testSong.getId());
        Mockito.verify(albumDao).deleteReviewsFromAlbum(ALBUM_ID);
        Mockito.verify(albumDao).delete(ALBUM_ID);
        Mockito.verify(imageService).delete(defaultImage.getId());
    }

    @Test(expected = AlbumNotFoundException.class)
    public void test_delete_albumNotFound() {
        // 1. Pre-conditions
        Mockito.when(albumDao.findById(ALBUM_ID)).thenReturn(Optional.empty());

        // 2. Execute
        albumService.delete(ALBUM_ID);

        // 3. Post-conditions - exception thrown
    }

    // ========== FIND TESTS ==========

    @Test
    public void test_findById_successful() {
        // 1. Pre-conditions
        Mockito.when(albumDao.findById(ALBUM_ID)).thenReturn(Optional.of(testAlbum));

        // 2. Execute
        Album found = albumService.findById(ALBUM_ID);

        // 3. Post-conditions
        assertNotNull(found);
        assertEquals(ALBUM_ID, found.getId().longValue());
        assertEquals(TITLE, found.getTitle());
    }

    @Test(expected = AlbumNotFoundException.class)
    public void test_findById_notFound() {
        // 1. Pre-conditions
        Mockito.when(albumDao.findById(ALBUM_ID)).thenReturn(Optional.empty());

        // 2. Execute
        albumService.findById(ALBUM_ID);

        // 3. Post-conditions - exception thrown
    }

    // ========== RATING TESTS ==========

    @Test
    public void test_updateRating_successful() {
        // 1. Pre-conditions
        AlbumReview review1 = new AlbumReview();
        review1.setRating(5);
        AlbumReview review2 = new AlbumReview();
        review2.setRating(4);
        List<AlbumReview> reviews = Arrays.asList(review1, review2);

        Mockito.when(albumDao.findReviewsByAlbumId(ALBUM_ID)).thenReturn(new ArrayList<>(reviews));
        Mockito.when(albumDao.updateRating(eq(ALBUM_ID), anyDouble(), anyInt())).thenReturn(true);

        // 2. Execute
        Boolean result = albumService.updateRating(ALBUM_ID);

        // 3. Post-conditions
        assertTrue(result);
        Mockito.verify(albumDao).updateRating(ALBUM_ID, 4.5, 2);
    }

    @Test
    public void test_hasUserReviewed_returnsTrue() {
        // 1. Pre-conditions
        Mockito.when(albumDao.hasUserReviewed(USER_ID, ALBUM_ID)).thenReturn(true);

        // 2. Execute
        Boolean result = albumService.hasUserReviewed(USER_ID, ALBUM_ID);

        // 3. Post-conditions
        assertTrue(result);
    }
}
