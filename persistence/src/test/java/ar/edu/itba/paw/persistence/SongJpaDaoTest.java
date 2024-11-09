package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
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
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "classpath:song_setUp.sql")
public class SongJpaDaoTest {

    private static final long PRE_EXISTING_IMAGE_ID = 100;
    private static final long PRE_EXISTING_USER_ID = 200;
    private static final long PRE_EXISTING_ARTIST_ID = 300;
    private static final long PRE_EXISTING_ARTIST_2_ID = 301;
    private static final long PRE_EXISTING_REVIEW_ID = 400;
    private static final long PRE_EXISTING_ALBUM_ID = 500;
    private static final long PRE_EXISTING_ALBUM_2_ID = 501;
    private static final long PRE_EXISTING_SONG_ID = 600;
    private static final long PRE_EXISTING_SONG_2_ID = 601;

    private static final long NEW_ARTIST_ID = 1000;
    private static final long NEW_ALBUM_ID = 1000;
    private static final long NEW_SONG_ID = 1000;

    private static final String PRE_EXISTING_SONG_TITLE = "DummySong";
    private static final String PRE_EXISTING_SONG_DURATION = "1:45";
    private static final int PRE_EXISTING_SONG_TRACK_NUMBER = 1;

    private static final String NEW_SONG_TITLE = "DummyX";
    private static final String NEW_SONG_DURATION = "1:23";
    private static final int NEW_SONG_TRACK_NUMBER = 7;
    private static final Double NEW_SONG_AVG_RATING = 3.4;
    private static final int NEW_SONG_RATING_AMOUNT = 15;

    @Autowired
    private SongDao songDao;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void test_find() {
        // 1. Pre-conditions - the song exist

        // 2. Execute
        Optional<Song> maybeSong = songDao.find(PRE_EXISTING_SONG_ID);

        // 3. Post-conditions
        assertTrue(maybeSong.isPresent());
        assertEquals(PRE_EXISTING_SONG_ID, maybeSong.get().getId().longValue());
        assertEquals(PRE_EXISTING_SONG_TITLE, maybeSong.get().getTitle());
        assertEquals(PRE_EXISTING_SONG_DURATION, maybeSong.get().getDuration());
        assertEquals(PRE_EXISTING_SONG_TRACK_NUMBER, maybeSong.get().getTrackNumber().intValue());
    }

    @Test(expected = NoSuchElementException.class)
    public void test_find_NoSong() {
        // 1. Pre-conditions - the song does not exist

        // 2. Execute
        Optional<Song> maybeSong = songDao.find(NEW_SONG_ID);

        // 3. Post-conditions
        assertFalse(maybeSong.isPresent());
        assertEquals(NEW_SONG_ID, maybeSong.get().getId().longValue());
    }

    @Test
    public void test_findByArtistId() {
        // 1. Pre-conditions - 3 songs exist in database from artist 301
        int offset = 0;
        int pageSize = 10;

        // 2. Execute
        List<Song> songList = songDao.findByArtistId(PRE_EXISTING_ARTIST_2_ID, pageSize, offset);

        // 3. Post-conditions
        assertEquals(5, songList.size());
    }

    @Test
    public void test_findByArtistId_NoArtist() {
        // 1. Pre-conditions - artist does not exist
        int page = 1;
        int pageSize = 10;

        // 2. Execute
        List<Song> songList = songDao.findByArtistId(NEW_ARTIST_ID, pageSize, page);

        // 3. Post-conditions
        assertEquals(0, songList.size());
    }

    @Test
    public void test_findByAlbumId() {
        // 1. Pre-conditions - album exist and has 5 songs

        // 2. Execute
        List<Song> songList = songDao.findByAlbumId(PRE_EXISTING_ALBUM_2_ID);

        // 3. Post-conditions
        assertEquals(5, songList.size());
    }

    @Test
    public void test_findByAlbumId_NoAlbum() {
        // 1. Pre-conditions - album does not exist

        // 2. Execute
        List<Song> songList = songDao.findByAlbumId(NEW_ALBUM_ID);

        // 3. Post-conditions
        assertEquals(0, songList.size());
    }

    @Test
    public void test_findByTitleContaining() {
        // 1. Pre-conditions - song with substring exists

        // 2. Execute
        List<Song> songList = songDao.findByTitleContaining(PRE_EXISTING_SONG_TITLE.substring(3,7)); // mySo

        // 3. Post-conditions
        assertEquals(4, songList.size());
        for (Song song : songList) {
            assertTrue(song.getTitle().contains(PRE_EXISTING_SONG_TITLE.substring(3,7)));
        }
    }

    @Test
    public void test_findByTitleContaining_NoSong() {
        // 1. Pre-conditions - song does not exist with substring

        // 2. Execute
        List<Song> songList = songDao.findByTitleContaining("Nothing");

        // 3. Post-conditions
        assertEquals(0, songList.size());
    }

    @Test
    public void test_findAll() {
        // 1. Pre-conditions - Only 5 songs exist in database

        // 2. Execute
        List<Song> songList = songDao.findAll();

        // 3. Post-conditions
        assertEquals(6, songList.size());
    }

    @Test
    public void test_findPaginated() {
        // 1. Pre-conditions - 5 songs exist in database

        // 2. Execute
        List<Song> songList = songDao.findPaginated(FilterType.RATING, 3,1);

        // 3. Post-conditions
        assertEquals(3, songList.size()); //Correct limit
        assertEquals(PRE_EXISTING_SONG_2_ID, songList.getFirst().getId().longValue());  //Correct offset
    }

    @Test
    public void test_findPaginated_endPage() {
        // 1. Pre-conditions - 5 songs exist in database

        // 2. Execute
        List<Song> songList = songDao.findPaginated(FilterType.RATING, 4,3);

        // 3. Post-conditions
        assertEquals(3, songList.size());
    }



    @Test
    public void test_create() {
        // 1. Pre-conditions - the
        Album album = new Album(PRE_EXISTING_ALBUM_ID);
        Song song = new Song(NEW_SONG_TITLE, NEW_SONG_DURATION, NEW_SONG_TRACK_NUMBER, album,0,0.0);

        // 2. Execute
        Song songCreated = songDao.create(song);

        // 3. Post-conditions

        // Check if returned song has expected value
        assertEquals(song.getTitle(), songCreated.getTitle());
        assertEquals(song.getDuration(), songCreated.getDuration());
        assertEquals(song.getTrackNumber(), songCreated.getTrackNumber());
        assertEquals(PRE_EXISTING_ALBUM_ID, songCreated.getAlbum().getId().longValue());
        assertEquals(0, songCreated.getAvgRating(),0);
        assertEquals(0, songCreated.getRatingCount().intValue());

        // check if song is saved correctly in database
        assertEquals(1,em.createQuery("SELECT COUNT(s) FROM Song s " +
                                "WHERE s.title = :title " +
                                "AND s.duration = :duration " +
                                "AND s.trackNumber = :trackNumber " +
                                "AND s.avgRating = 0 " +
                                "AND s.ratingCount = 0",
                        Long.class)
                .setParameter("title", NEW_SONG_TITLE)
                .setParameter("duration", NEW_SONG_DURATION)
                .setParameter("trackNumber", NEW_SONG_TRACK_NUMBER)
                .getSingleResult().intValue());
    }

    @Test
    public void test_saveSongArtist() {
        // 1. Pre-conditions - the song exist and artist exist
        Artist artist = new Artist(PRE_EXISTING_ARTIST_ID);
        Song song = new Song(PRE_EXISTING_SONG_2_ID);

        // 2. Execute
        int rowsChanged = songDao.saveSongArtist(song, artist);

        // 3. Post-conditions
        assertEquals(1, rowsChanged);
    }

    @Test
    public void test_saveSongArtist_DuplicateKey() {
        // 1. Pre-conditions - the song exist and artist exist
        Artist artist = new Artist(PRE_EXISTING_ARTIST_ID);
        Song song = new Song(PRE_EXISTING_SONG_ID);

        // 2. Execute
        int rowsChanged = songDao.saveSongArtist(song, artist);

        // 3. Post-conditions
        assertEquals(1, rowsChanged);

        TypedQuery<Song> query = em.createQuery("SELECT s FROM Song s JOIN s.artists WHERE s.id = :songId", Song.class).setParameter("songId", PRE_EXISTING_SONG_ID);
        boolean isDuplicate = false;
        for(Artist a : query.getSingleResult().getArtists()) {
            if(a.getId().equals(PRE_EXISTING_ARTIST_ID)) {
                if(isDuplicate) {
                    assertTrue(isDuplicate);
                }
                isDuplicate = true;
            }
        }
    }

    @Test
    public void test_update() {
        // 1. Pre-conditions - the song exist
        Album album = new Album(PRE_EXISTING_ALBUM_ID);
        Song song = new Song(PRE_EXISTING_SONG_ID, NEW_SONG_TITLE, NEW_SONG_DURATION, NEW_SONG_TRACK_NUMBER, album, NEW_SONG_RATING_AMOUNT, NEW_SONG_AVG_RATING);

        // 2. Execute
        Song songUpdated = songDao.update(song);

        // 3. Post-conditions

        // Check if returned song has expected value
        assertEquals(song.getTitle(), songUpdated.getTitle());
        assertEquals(song.getDuration(), songUpdated.getDuration());
        assertEquals(song.getTrackNumber(), songUpdated.getTrackNumber());
        assertEquals(PRE_EXISTING_ALBUM_ID, songUpdated.getAlbum().getId().longValue());
        assertEquals(song.getAvgRating(), songUpdated.getAvgRating());
        assertEquals(song.getRatingCount(), songUpdated.getRatingCount());

        // Check if song is saved correctly in database
        assertEquals(1,em.createQuery("SELECT COUNT(s) FROM Song s " +
                            "JOIN s.album " +
                                "WHERE s.id = :songId " +
                                "AND s.title = :title " +
                                "AND s.duration = :duration " +
                                "AND s.trackNumber = :trackNumber " +
                                "AND s.ratingCount = :ratingCount " +
                                "AND s.avgRating = :avgRating " +
                                "AND s.album.id = :albumId",
                        Long.class)
                .setParameter("songId", PRE_EXISTING_SONG_ID)
                .setParameter("title", NEW_SONG_TITLE)
                .setParameter("duration", NEW_SONG_DURATION)
                .setParameter("trackNumber", NEW_SONG_TRACK_NUMBER)
                .setParameter("ratingCount", NEW_SONG_RATING_AMOUNT)
                .setParameter("avgRating", NEW_SONG_AVG_RATING)
                .setParameter("albumId", album.getId())
                .getSingleResult().intValue());
    }

    @Test
    public void test_updateRating() {
        // 1. Pre-conditions - the song exist

        // 2. Execute
        boolean updated = songDao.updateRating(PRE_EXISTING_SONG_ID, NEW_SONG_AVG_RATING, NEW_SONG_RATING_AMOUNT);

        // 3. Post-conditions
        assertTrue(updated);
        assertEquals(1,em.createQuery("SELECT COUNT(s) FROM Song s " +
                                "WHERE s.id = :songId " +
                                "AND s.avgRating = :avgRating " +
                                "AND s.ratingCount = :ratingCount",
                        Long.class)
                .setParameter("songId", PRE_EXISTING_SONG_ID)
                .setParameter("avgRating", NEW_SONG_AVG_RATING)
                .setParameter("ratingCount", NEW_SONG_RATING_AMOUNT)
                .getSingleResult().intValue());
    }

    @Test
    public void test_hasUserReviewed_Yes() {
        // 1. Pre-conditions - the user made a review about song

        // 2. Execute
        boolean deleted = songDao.hasUserReviewed(PRE_EXISTING_USER_ID, PRE_EXISTING_SONG_ID);

        // 3. Post-conditions
        assertTrue(deleted);
    }

    @Test
    public void test_hasUserReviewed_No() {
        // 1. Pre-conditions - the user did not make a review about song

        // 2. Execute
        boolean deleted = songDao.hasUserReviewed(PRE_EXISTING_USER_ID, PRE_EXISTING_SONG_2_ID);

        // 3. Post-conditions
        assertFalse(deleted);
    }

    @Test
    public void test_delete() {
        // 1. Pre-conditions - the song exist

        // 2. Execute
        boolean deleted = songDao.delete(PRE_EXISTING_SONG_ID);

        // 3. Post-conditions
        assertTrue(deleted);
        assertNull(em.find(Song.class, PRE_EXISTING_SONG_ID));
    }

    @Test
    public void test_delete_NoSong() {
        // 1. Pre-conditions - the song does not exist

        // 2. Execute
        boolean deleted = songDao.delete(NEW_SONG_ID);

        // 3. Post-conditions
        assertFalse(deleted);
    }

    @Test
    public void test_deleteReviewsFromSong_Yes() {
        // 1. Pre-conditions - song has reviews

        // 2. Execute
        boolean deleted = songDao.deleteReviewsFromSong(PRE_EXISTING_SONG_ID);

        // 3. Post-conditions
        assertTrue(deleted);
    }

    @Test
    public void test_deleteReviewsFromSong_No() {
        // 1. Pre-conditions - song does not have reviews

        // 2. Execute
        boolean deleted = songDao.deleteReviewsFromSong(NEW_SONG_ID);

        // 3. Post-conditions
        assertFalse(deleted);
    }

    @Test
    public void test_findReviewsBySongId() {
        // 1. Pre-conditions - song has review

        // 2. Execute
        List<SongReview> songReviewList = songDao.findReviewsBySongId(PRE_EXISTING_SONG_ID);

        // 3. Post-conditions
        assertEquals(1, songReviewList.size());
    }

    @Test
    public void test_findReviewsBySongId_NoReview() {
        // 1. Pre-conditions - song has review

        // 2. Execute
        List<SongReview> songReviewList = songDao.findReviewsBySongId(NEW_SONG_ID);

        // 3. Post-conditions
        assertEquals(0, songReviewList.size());
    }
}
