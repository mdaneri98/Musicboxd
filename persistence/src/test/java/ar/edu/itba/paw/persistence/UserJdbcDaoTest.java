package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.jdbc.UserJdbcDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.*;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "classpath:cuser_setUp.sql")
public class UserJdbcDaoTest {

    private static final long PRE_EXISTING_IMAGE_ID = 100;
    private static final long PRE_EXISTING_USER_ID = 200;
    private static final long PRE_EXISTING_USER_2_ID = 201;
    private static final long PRE_EXISTING_ARTIST_ID = 300;
    private static final long PRE_EXISTING_ARTIST_2_ID = 301;
    private static final long PRE_EXISTING_REVIEW_ID = 400;
    private static final long PRE_EXISTING_ALBUM_ID = 500;
    private static final long PRE_EXISTING_ALBUM_2_ID = 501;
    private static final long PRE_EXISTING_SONG_ID = 600;
    private static final long PRE_EXISTING_SONG_2_ID = 601;

    private static final String PRE_EXISTING_USERNAME = "Dummy";
    private static final String PRE_EXISTING_EMAIL = "dummy@example.com";
    private static final String PRE_EXISTING_PASSWORD = "dummy123";

    private static final String PRE_EXISTING_USERNAME_2 = "Dummy1";
    private static final String PRE_EXISTING_EMAIL_2 = "dummy1@example.com";

    private static final long NEW_USER_ID = 1000;
    private static final long NEW_ARTIST_ID = 1000;
    private static final long NEW_ALBUM_ID = 1000;
    private static final long NEW_SONG_ID = 1000;

    private static final String NEW_USERNAME = "DummyX";
    private static final String NEW_EMAIL = "dummyX@example.com";
    private static final String NEW_PASSWORD = "dummyX123";
    private static final String NEW_NAME = "DummyXName";
    private static final String NEW_BIO = "DummyXBio";
    private static final boolean VERIFIED_TRUE = true;
    private static final boolean MODERATOR_TRUE = true;
    private static final int USER_FOLLOWERS = 5;
    private static final int USER_FOLLOWING = 6;
    private static final int REVIEW_AMOUNT = 8;


    @Autowired
    private UserJdbcDao userDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void test_find_ExistingUser() {
        // 1. Pre-conditions - the user exist

        // 2. Execute
        Optional<User> maybeUser = userDao.find(PRE_EXISTING_USER_ID);

        // 3. Post-conditions
        assertTrue(maybeUser.isPresent());
        assertEquals(PRE_EXISTING_USER_ID, maybeUser.get().getId().longValue());
        assertEquals(PRE_EXISTING_EMAIL, maybeUser.get().getEmail());
        assertEquals(PRE_EXISTING_USERNAME, maybeUser.get().getUsername());
        assertEquals(PRE_EXISTING_IMAGE_ID, maybeUser.get().getImgId().longValue());
    }

    @Test(expected = NoSuchElementException.class)
    public void test_find_NonExistingUser() {
        // 1. Pre-conditions - the user does not exist

        // 2. Execute
        Optional<User> maybeUser = userDao.find(NEW_USER_ID);

        // 3. Post-conditions
        assertFalse(maybeUser.isPresent());
        assertEquals(NEW_USER_ID, maybeUser.get().getId().longValue());
    }

    @Test
    public void test_findAll() {
        // 1. Pre-conditions - Only 5 user exist in database

        // 2. Execute
        List<User> userList = userDao.findAll();

        // 3. Post-conditions
        assertEquals(5, userList.size());
    }

    @Test
    public void test_create() {
        // 1. Pre-conditions - no user with username or email exists

        // 2. Execute
        Optional<User> user = userDao.create(NEW_USERNAME, NEW_EMAIL, NEW_PASSWORD, PRE_EXISTING_IMAGE_ID);

        // 3. Post-conditions
        assertTrue(user.isPresent());

        assertEquals(NEW_USERNAME, user.get().getUsername());
        assertEquals(NEW_EMAIL, user.get().getEmail());
        assertEquals(NEW_PASSWORD, user.get().getPassword());
        assertNull(user.get().getName());
        assertNull(user.get().getBio());
        assertNotNull(user.get().getCreatedAt());
        assertNotNull(user.get().getUpdatedAt());
        assertEquals(PRE_EXISTING_IMAGE_ID, user.get().getImgId().longValue());
        assertEquals(0, user.get().getFollowersAmount().intValue());
        assertEquals(0, user.get().getFollowingAmount().intValue());
        assertEquals(0, user.get().getReviewAmount().intValue());
        assertFalse(user.get().isModerator());
        assertFalse(user.get().isVerified());

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"cuser",
                String.format("username = '%s'" +
                                " AND email = '%s'" +
                                " AND password = '%s'" +
                                " AND name IS NULL" +
                                " AND bio IS NULL" +
                                " AND created_at IS NOT NULL" +
                                " AND updated_at IS NOT NULL" +
                                " AND img_id = '%d'" +
                                " AND followers_amount = 0" +
                                " AND following_amount = 0" +
                                " AND review_amount = 0" +
                                " AND moderator = 'false'" +
                                " AND verified = 'false'",
                        NEW_USERNAME,
                        NEW_EMAIL,
                        NEW_PASSWORD,
                        PRE_EXISTING_IMAGE_ID)));
    }

    @Test(expected = DuplicateKeyException.class)
    public void test_create_duplicateUsername() {
        // 1. Pre-conditions - User with the same username exists

        // 2. Execute
        Optional<User> user = userDao.create(PRE_EXISTING_USERNAME, NEW_EMAIL, NEW_PASSWORD, PRE_EXISTING_IMAGE_ID);

        // 3. Post-conditions
        assertFalse(user.isPresent());
    }

    @Test(expected = DuplicateKeyException.class)
    public void test_create_duplicateEmail() {
        // 1. Pre-conditions - User with the same email exists

        // 2. Execute
        Optional<User> user = userDao.create(NEW_USERNAME, PRE_EXISTING_EMAIL, NEW_PASSWORD, PRE_EXISTING_IMAGE_ID);

        // 3. Post-conditions
        assertFalse(user.isPresent());
    }

    @Test
    public void test_isFollowing_Yes() {
        // 1. Pre-conditions - first user follows second user

        // 2. Execute
        boolean following = userDao.isFollowing(PRE_EXISTING_USER_ID, PRE_EXISTING_USER_2_ID);

        // 3. Post-conditions
        assertTrue(following);
    }

    @Test
    public void test_isFollowing_No() {
        // 1. Pre-conditions - first user does not follow second user

        // 2. Execute
        boolean following = userDao.isFollowing(PRE_EXISTING_USER_2_ID, PRE_EXISTING_USER_ID);

        // 3. Post-conditions
        assertFalse(following);
    }

    @Test
    public void test_isFollowing_NoUser1() {
        // 1. Pre-conditions - first user does not exist

        // 2. Execute
        boolean following = userDao.isFollowing(NEW_USER_ID, PRE_EXISTING_USER_ID);

        // 3. Post-conditions
        assertFalse(following);
    }

    @Test
    public void test_isFollowing_NoUser2() {
        // 1. Pre-conditions - second user does not exist

        // 2. Execute
        boolean following = userDao.isFollowing(PRE_EXISTING_USER_ID, NEW_USER_ID);

        // 3. Post-conditions
        assertFalse(following);
    }

    @Test
    public void test_isArtistFavorite_Yes() {
        // 1. Pre-conditions - artist is user favorite

        // 2. Execute
        boolean favorite = userDao.isArtistFavorite(PRE_EXISTING_USER_ID, PRE_EXISTING_ARTIST_ID);

        // 3. Post-conditions
        assertTrue(favorite);
    }

    @Test
    public void test_isArtistFavorite_No() {
        // 1. Pre-conditions - artist is not user favorite

        // 2. Execute
        boolean favorite = userDao.isArtistFavorite(PRE_EXISTING_USER_ID, PRE_EXISTING_ARTIST_2_ID);

        // 3. Post-conditions
        assertFalse(favorite);
    }

    @Test
    public void test_isArtistFavorite_NoUser() {
        // 1. Pre-conditions - artist does not exist

        // 2. Execute
        boolean favorite = userDao.isArtistFavorite(NEW_USER_ID, PRE_EXISTING_ARTIST_ID);

        // 3. Post-conditions
        assertFalse(favorite);
    }

    @Test
    public void test_isArtistFavorite_NoArtist() {
        // 1. Pre-conditions - user does not exist

        // 2. Execute
        boolean favorite = userDao.isArtistFavorite(PRE_EXISTING_USER_ID, NEW_ARTIST_ID);

        // 3. Post-conditions
        assertFalse(favorite);
    }

    @Test
    public void test_isAlbumFavorite_Yes() {
        // 1. Pre-conditions - album is user favorite

        // 2. Execute
        boolean favorite = userDao.isAlbumFavorite(PRE_EXISTING_USER_ID, PRE_EXISTING_ALBUM_ID);

        // 3. Post-conditions
        assertTrue(favorite);
    }

    @Test
    public void test_isAlbumFavorite_No() {
        // 1. Pre-conditions - album is not user favorite

        // 2. Execute
        boolean favorite = userDao.isAlbumFavorite(PRE_EXISTING_USER_ID, PRE_EXISTING_ALBUM_2_ID);

        // 3. Post-conditions
        assertFalse(favorite);
    }

    @Test
    public void test_isAlbumFavorite_NoUser() {
        // 1. Pre-conditions - album does not exist

        // 2. Execute
        boolean favorite = userDao.isAlbumFavorite(NEW_USER_ID, PRE_EXISTING_ALBUM_ID);

        // 3. Post-conditions
        assertFalse(favorite);
    }

    @Test
    public void test_isAlbumFavorite_NoAlbum() {
        // 1. Pre-conditions - user does not exist

        // 2. Execute
        boolean favorite = userDao.isAlbumFavorite(PRE_EXISTING_USER_ID, NEW_ALBUM_ID);

        // 3. Post-conditions
        assertFalse(favorite);
    }

    @Test
    public void test_isSongFavorite_Yes() {
        // 1. Pre-conditions - song is user favorite

        // 2. Execute
        boolean favorite = userDao.isSongFavorite(PRE_EXISTING_USER_ID, PRE_EXISTING_SONG_ID);

        // 3. Post-conditions
        assertTrue(favorite);
    }

    @Test
    public void test_isSongFavorite_No() {
        // 1. Pre-conditions - song is not user favorite

        // 2. Execute
        boolean favorite = userDao.isSongFavorite(PRE_EXISTING_USER_ID, PRE_EXISTING_SONG_2_ID);

        // 3. Post-conditions
        assertFalse(favorite);
    }

    @Test
    public void test_isSongFavorite_NoUser() {
        // 1. Pre-conditions - song does not exist

        // 2. Execute
        boolean favorite = userDao.isSongFavorite(NEW_USER_ID, PRE_EXISTING_SONG_ID);

        // 3. Post-conditions
        assertFalse(favorite);
    }

    @Test
    public void test_isSongFavorite_NoSong() {
        // 1. Pre-conditions - user does not exist

        // 2. Execute
        boolean favorite = userDao.isSongFavorite(PRE_EXISTING_USER_ID, NEW_SONG_ID);

        // 3. Post-conditions
        assertFalse(favorite);
    }

    @Test
    public void test_updateUserReviewAmount () {
        // 1. Pre-conditions - user exists with 3 reviews

        // 2. Execute
        userDao.updateUserReviewAmount(PRE_EXISTING_USER_ID);

        // 3. Post-conditions
        assertEquals(3, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"review",
                String.format("user_id = '%d'", PRE_EXISTING_USER_ID)));
    }

    @Test
    public void test_getFollowers () {
        // 1. Pre-conditions - user has 3 followers

        // 2. Execute
        List<User> userList = userDao.getFollowers(PRE_EXISTING_USER_ID, 5, 1);

        // 3. Post-conditions
        assertEquals(2, userList.size());
    }

    @Test
    public void test_getFollowing () {
        // 1. Pre-conditions - user is following 3 other users

        // 2. Execute
        List<User> userList = userDao.getFollowing(PRE_EXISTING_USER_2_ID, 2, 0);

        // 3. Post-conditions
        assertEquals(2, userList.size());
    }

    @Test
    public void test_createFollowing () {
        // 1. Pre-conditions - first user is not following second user
        User user1 = new User(PRE_EXISTING_USER_2_ID, PRE_EXISTING_USERNAME_2, PRE_EXISTING_EMAIL_2, PRE_EXISTING_PASSWORD);
        User user2 = new User(PRE_EXISTING_USER_ID, PRE_EXISTING_USERNAME, PRE_EXISTING_EMAIL, PRE_EXISTING_PASSWORD);

        // 2. Execute
        int rowsChanged = userDao.createFollowing(user1, user2);

        // 3. Post-conditions
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"follower",
                String.format("user_id = '%d' AND following = '%d'", PRE_EXISTING_USER_ID, PRE_EXISTING_USER_2_ID)));
        assertEquals(1, rowsChanged);
    }

    @Test(expected = DuplicateKeyException.class)
    public void test_createFollowing_AlreadyFollowing () {
        // 1. Pre-conditions - first user is following second user
        User user1 = new User(PRE_EXISTING_USER_ID, PRE_EXISTING_USERNAME, PRE_EXISTING_EMAIL, PRE_EXISTING_PASSWORD);
        User user2 = new User(PRE_EXISTING_USER_2_ID, PRE_EXISTING_USERNAME_2, PRE_EXISTING_EMAIL_2, PRE_EXISTING_PASSWORD);

        // 2. Execute
        int rowsChanged = userDao.createFollowing(user1, user2);

        // 3. Post-conditions
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"follower",
                String.format("user_id = '%d' AND following = '%d'", PRE_EXISTING_USER_2_ID, PRE_EXISTING_USER_ID)));
        assertEquals(0, rowsChanged);
    }

    @Test
    public void test_undoFollowing () {
        // 1. Pre-conditions - first user is following the second user
        User user1 = new User(PRE_EXISTING_USER_ID, PRE_EXISTING_USERNAME, PRE_EXISTING_EMAIL, PRE_EXISTING_PASSWORD);
        User user2 = new User(PRE_EXISTING_USER_2_ID, PRE_EXISTING_USERNAME_2, PRE_EXISTING_EMAIL_2, PRE_EXISTING_PASSWORD);

        // 2. Execute
        int rowsChanged = userDao.undoFollowing(user1, user2);

        // 3. Post-conditions
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"follower",
                String.format("user_id = '%d' AND following = '%d'", PRE_EXISTING_USER_2_ID, PRE_EXISTING_USER_ID)));
        assertEquals(1, rowsChanged);
    }

    @Test
    public void test_undoFollowing_AlreadyNotFollowing () {
        // 1. Pre-conditions - first user is not following the second user
        User user1 = new User(PRE_EXISTING_USER_2_ID, PRE_EXISTING_USERNAME_2, PRE_EXISTING_EMAIL_2, PRE_EXISTING_PASSWORD);
        User user2 = new User(PRE_EXISTING_USER_ID, PRE_EXISTING_USERNAME, PRE_EXISTING_EMAIL, PRE_EXISTING_PASSWORD);

        // 2. Execute
        int rowsChanged = userDao.undoFollowing(user1, user2);

        // 3. Post-conditions
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"follower",
                String.format("user_id = '%d' AND following = '%d'", PRE_EXISTING_USER_2_ID, PRE_EXISTING_USER_ID)));
        assertEquals(0, rowsChanged);
    }

    @Test
    public void test_update () {
        // 1. Pre-conditions - user exists
        User user = new User(PRE_EXISTING_USER_ID, NEW_USERNAME, NEW_EMAIL, NEW_PASSWORD, NEW_NAME, NEW_BIO, VERIFIED_TRUE, PRE_EXISTING_IMAGE_ID, MODERATOR_TRUE, USER_FOLLOWERS, USER_FOLLOWING, REVIEW_AMOUNT);

        // 2. Execute
        int rowsChanged = userDao.update(user);

        // 3. Post-conditions
        assertEquals(1, rowsChanged);

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"cuser",
                String.format("id = '%d'" +
                                " AND username = '%s'" +
                                " AND email = '%s'" +
                                " AND name = '%s'" +
                                " AND bio = '%s'" +
                                " AND created_at IS NOT NULL" +
                                " AND updated_at IS NOT NULL" +
                                " AND img_id = '%d'" +
                                " AND followers_amount = '%d'" +
                                " AND following_amount = '%d'" +
                                " AND review_amount = '%d'" +
                                " AND moderator = 'true'" +
                                " AND verified = 'true'",
                        PRE_EXISTING_USER_ID,
                        NEW_USERNAME,
                        NEW_EMAIL,
                        NEW_NAME,
                        NEW_BIO,
                        PRE_EXISTING_IMAGE_ID,
                        USER_FOLLOWERS,
                        USER_FOLLOWING,
                        REVIEW_AMOUNT)));
    }

    @Test
    public void test_changePassword () {
        // 1. Pre-conditions - user exists

        // 2. Execute
        boolean updated = userDao.changePassword(PRE_EXISTING_USER_ID, NEW_PASSWORD);

        // 3. Post-conditions
        assertTrue(updated);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"cuser",
                String.format("id = '%d' AND password = '%s'", PRE_EXISTING_USER_ID, NEW_PASSWORD)));
    }

    @Test
    public void test_changePassword_NoUser () {
        // 1. Pre-conditions - user exists

        // 2. Execute
        boolean updated = userDao.changePassword(NEW_USER_ID, NEW_PASSWORD);

        // 3. Post-conditions
        assertFalse(updated);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"cuser",
                String.format("id = '%d' AND password = '%s'", NEW_USER_ID, NEW_PASSWORD)));
    }

    @Test
    public void test_findByEmail () {
        // 1. Pre-conditions - user exists

        // 2. Execute
        Optional<User> maybeUser = userDao.findByEmail(PRE_EXISTING_EMAIL);

        // 3. Post-conditions
        assertTrue(maybeUser.isPresent());
        assertEquals(PRE_EXISTING_EMAIL, maybeUser.get().getEmail());
    }

    @Test(expected = NoSuchElementException.class)
    public void test_findByEmail_NewEmail () {
        // 1. Pre-conditions - user exists

        // 2. Execute
        Optional<User> maybeUser = userDao.findByEmail(NEW_EMAIL);

        // 3. Post-conditions
        assertFalse(maybeUser.isPresent());
        assertEquals(NEW_EMAIL, maybeUser.get().getEmail());
    }

    @Test
    public void test_findByUsername () {
        // 1. Pre-conditions - user exists

        // 2. Execute
        Optional<User> maybeUser = userDao.findByUsername(PRE_EXISTING_USERNAME);

        // 3. Post-conditions
        assertTrue(maybeUser.isPresent());
        assertEquals(PRE_EXISTING_USERNAME, maybeUser.get().getUsername());
    }

    @Test(expected = NoSuchElementException.class)
    public void test_findByEmail_NewUsername () {
        // 1. Pre-conditions - user exists

        // 2. Execute
        Optional<User> maybeUser = userDao.findByEmail(NEW_USERNAME);

        // 3. Post-conditions
        assertFalse(maybeUser.isPresent());
        assertEquals(NEW_USERNAME, maybeUser.get().getUsername());
    }

    @Test
    public void test_deleteById () {
        // 1. Pre-conditions - user exists

        // 2. Execute
        int rowsChanged = userDao.deleteById(PRE_EXISTING_USER_ID);

        // 3. Post-conditions
        assertEquals(1, rowsChanged);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"cuser",
                String.format("id = '%d'", PRE_EXISTING_USER_ID)));
    }

    @Test
    public void test_deleteById_NoUser () {
        // 1. Pre-conditions - user does not exists

        // 2. Execute
        int rowsChanged = userDao.deleteById(NEW_USER_ID);

        // 3. Post-conditions
        assertEquals(0, rowsChanged);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"cuser",
                String.format("id = '%d'", NEW_USER_ID)));
    }

    @Test
    public void test_getFavoriteArtists () {
        // 1. Pre-conditions - user exists and has 1 favorite artist

        // 2. Execute
        List<Artist> artistList = userDao.getFavoriteArtists(PRE_EXISTING_USER_ID);

        // 3. Post-conditions
        assertEquals(1, artistList.size());
    }

    @Test
    public void test_getFavoriteArtists_NoUser () {
        // 1. Pre-conditions - user does not exists

        // 2. Execute
        List<Artist> artistList = userDao.getFavoriteArtists(NEW_USER_ID);

        // 3. Post-conditions
        assertEquals(0, artistList.size());
    }

    @Test
    public void test_addFavoriteArtist () {
        // 1. Pre-conditions - user exists and artist is not favorite

        // 2. Execute
        boolean updated = userDao.addFavoriteArtist(PRE_EXISTING_USER_ID, PRE_EXISTING_ARTIST_2_ID);

        // 3. Post-conditions
        assertTrue(updated);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"favorite_artist",
                String.format("user_id = '%d' AND artist_id = '%d'", PRE_EXISTING_USER_ID, PRE_EXISTING_ARTIST_2_ID)));
    }

    @Test
    public void test_addFavoriteArtist_MoreThan5 () {
        // 1. Pre-conditions - user exists and has 5 favorite artists

        // 2. Execute
        boolean updated = userDao.addFavoriteArtist(PRE_EXISTING_USER_2_ID, PRE_EXISTING_ARTIST_ID);

        // 3. Post-conditions
        assertFalse(updated);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"favorite_artist",
                String.format("user_id = '%d' AND artist_id = '%d'", PRE_EXISTING_USER_2_ID, PRE_EXISTING_ARTIST_ID)));
    }

    @Test
    public void test_addFavoriteArtist_NoUser () {
        // 1. Pre-conditions - user does not exist

        // 2. Execute
        boolean updated = userDao.addFavoriteArtist(NEW_USER_ID, PRE_EXISTING_ARTIST_2_ID);

        // 3. Post-conditions
        assertFalse(updated);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"favorite_artist",
                String.format("user_id = '%d' AND artist_id = '%d'", NEW_USER_ID, PRE_EXISTING_ARTIST_2_ID)));
    }

    @Test
    public void test_addFavoriteArtist_NoArtist () {
        // 1. Pre-conditions - artist does not exist

        // 2. Execute
        boolean updated = userDao.addFavoriteArtist(PRE_EXISTING_USER_ID, NEW_ARTIST_ID);

        // 3. Post-conditions
        assertFalse(updated);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"favorite_artist",
                String.format("user_id = '%d' AND artist_id = '%d'", PRE_EXISTING_USER_ID, NEW_ARTIST_ID)));
    }

@Test
public void test_getFavoriteAlbums () {
    // 1. Pre-conditions - user exists and has 1 favorite album

    // 2. Execute
    List<Album> albumList = userDao.getFavoriteAlbums(PRE_EXISTING_USER_ID);

    // 3. Post-conditions
    assertEquals(1, albumList.size());
}

    @Test
    public void test_getFavoriteAlbums_NoUser () {
        // 1. Pre-conditions - user does not exists

        // 2. Execute
        List<Album> albumList = userDao.getFavoriteAlbums(NEW_USER_ID);

        // 3. Post-conditions
        assertEquals(0, albumList.size());
    }

    @Test
    public void test_addFavoriteAlbum () {
        // 1. Pre-conditions - user exists and album is not favorite yet

        // 2. Execute
        boolean updated = userDao.addFavoriteAlbum(PRE_EXISTING_USER_ID, PRE_EXISTING_ALBUM_2_ID);

        // 3. Post-conditions
        assertTrue(updated);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"favorite_album",
                String.format("user_id = '%d' AND album_id = '%d'", PRE_EXISTING_USER_ID, PRE_EXISTING_ALBUM_2_ID)));
    }

    @Test
    public void test_addFavoriteAlbum_MoreThan5 () {
        // 1. Pre-conditions - user exists and has 5 favorite albums

        // 2. Execute
        boolean updated = userDao.addFavoriteAlbum(PRE_EXISTING_USER_2_ID, PRE_EXISTING_ALBUM_ID);

        // 3. Post-conditions
        assertFalse(updated);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"favorite_album",
                String.format("user_id = '%d' AND album_id = '%d'", PRE_EXISTING_USER_2_ID, PRE_EXISTING_ALBUM_ID)));
    }

    @Test
    public void test_addFavoriteAlbum_NoUser () {
        // 1. Pre-conditions - user does not exist

        // 2. Execute
        boolean updated = userDao.addFavoriteAlbum(NEW_USER_ID, PRE_EXISTING_ALBUM_2_ID);

        // 3. Post-conditions
        assertFalse(updated);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"favorite_album",
                String.format("user_id = '%d' AND album_id = '%d'", NEW_USER_ID, PRE_EXISTING_ALBUM_2_ID)));
    }

    @Test
    public void test_addFavoriteArtist_NoAlbum () {
        // 1. Pre-conditions - Album does not exist

        // 2. Execute
        boolean updated = userDao.addFavoriteAlbum(PRE_EXISTING_USER_ID, NEW_ALBUM_ID);

        // 3. Post-conditions
        assertFalse(updated);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"favorite_album",
                String.format("user_id = '%d' AND album_id = '%d'", PRE_EXISTING_USER_ID, NEW_ALBUM_ID)));
    }

    @Test
    public void test_getFavoriteSongs () {
        // 1. Pre-conditions - user exists and has 1 favorite song

        // 2. Execute
        List<Song> songList = userDao.getFavoriteSongs(PRE_EXISTING_USER_ID);

        // 3. Post-conditions
        assertEquals(1, songList.size());
    }

    @Test
    public void test_getFavoriteSongs_NoUser () {
        // 1. Pre-conditions - user does not exists

        // 2. Execute
        List<Song> songList = userDao.getFavoriteSongs(NEW_USER_ID);

        // 3. Post-conditions
        assertEquals(0, songList.size());
    }

    @Test
    public void test_addFavoriteSong () {
        // 1. Pre-conditions - user exists and song is not favorite yet

        // 2. Execute
        boolean updated = userDao.addFavoriteSong(PRE_EXISTING_USER_ID, PRE_EXISTING_SONG_2_ID);

        // 3. Post-conditions
        assertTrue(updated);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"favorite_song",
                String.format("user_id = '%d' AND song_id = '%d'", PRE_EXISTING_USER_ID, PRE_EXISTING_SONG_2_ID)));
    }

    @Test
    public void test_addFavoriteSong_MoreThan5 () {
        // 1. Pre-conditions - user exists and has 5 favorite songs

        // 2. Execute
        boolean updated = userDao.addFavoriteSong(PRE_EXISTING_USER_2_ID, PRE_EXISTING_SONG_ID);

        // 3. Post-conditions
        assertFalse(updated);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"favorite_song",
                String.format("user_id = '%d' AND song_id = '%d'", PRE_EXISTING_USER_2_ID, PRE_EXISTING_SONG_ID)));
    }

    @Test
    public void test_addFavoriteSong_NoUser () {
        // 1. Pre-conditions - user does not exist

        // 2. Execute
        boolean updated = userDao.addFavoriteSong(NEW_USER_ID, PRE_EXISTING_SONG_2_ID);

        // 3. Post-conditions
        assertFalse(updated);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"favorite_song",
                String.format("user_id = '%d' AND song_id = '%d'", NEW_USER_ID, PRE_EXISTING_SONG_2_ID)));
    }

    @Test
    public void test_addFavoriteSong_NoSong () {
        // 1. Pre-conditions - Song does not exist

        // 2. Execute
        boolean updated = userDao.addFavoriteSong(PRE_EXISTING_USER_ID, NEW_SONG_ID);

        // 3. Post-conditions
        assertFalse(updated);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"favorite_song",
                String.format("user_id = '%d' AND song_id = '%d'", PRE_EXISTING_USER_ID, NEW_SONG_ID)));
    }
}
