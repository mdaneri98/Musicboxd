package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.hibernate.exception.ConstraintViolationException;
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
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "classpath:cuser_setUp.sql")
public class UserJpaDaoTest {

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

    private static final byte[] BYTES = new byte[] { (byte) 0xbe, (byte) 0xef };

    private static final String PRE_EXISTING_USERNAME = "Dummy";
    private static final String PRE_EXISTING_EMAIL = "dummy@example.com";
    private static final String PRE_EXISTING_PASSWORD = "dummy123";

    private static final String PRE_EXISTING_USERNAME_2 = "Dummy1";
    private static final String PRE_EXISTING_EMAIL_2 = "dummy1@example.com";

    private static final long NEW_IMAGE_ID = 1000;
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
    private static final String PREFERRED_LANGUAGE = "en";

    @Autowired
    private UserDao userDao;

    @PersistenceContext
    private EntityManager em;

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
        assertEquals(PRE_EXISTING_IMAGE_ID, maybeUser.get().getImage().getId().longValue());
    }

    @Test
    public void test_find_NonExistingUser() {
        // 1. Pre-conditions - the user does not exist

        // 2. Execute
        Optional<User> maybeUser = userDao.find(NEW_USER_ID);

        // 3. Post-conditions
        assertFalse(maybeUser.isPresent());
        assertNull(em.find(User.class, NEW_USER_ID));
    }

    @Test
    public void test_findAll() {
        // 1. Pre-conditions - Only 5 user exist in database

        // 2. Execute
        List<User> userList = userDao.findAll(1, 100);

        // 3. Post-conditions
        assertEquals(5, userList.size());
    }

    @Test
    public void test_update() {
        // 1. Pre-conditions - user exists
        Image image = new Image(NEW_IMAGE_ID, BYTES);
        User user = new User(PRE_EXISTING_USER_ID, NEW_USERNAME, NEW_EMAIL, NEW_PASSWORD, NEW_NAME, NEW_BIO, VERIFIED_TRUE, image, MODERATOR_TRUE, USER_FOLLOWERS, USER_FOLLOWING, REVIEW_AMOUNT, PREFERRED_LANGUAGE);

        // 2. Execute
        Optional<User> optionalUser = userDao.update(user);

        // 3. Post-conditions
        assertTrue(optionalUser.isPresent());
        User updatedUser = optionalUser.get();
        
        // Basic field validations
        assertEquals(NEW_USERNAME, updatedUser.getUsername());
        assertEquals(NEW_EMAIL, updatedUser.getEmail());
        assertEquals(NEW_PASSWORD, updatedUser.getPassword());
        assertEquals(NEW_NAME, updatedUser.getName());
        assertEquals(NEW_BIO, updatedUser.getBio());
        assertEquals(NEW_IMAGE_ID, updatedUser.getImage().getId().longValue());
        assertEquals(USER_FOLLOWERS, updatedUser.getFollowersAmount().intValue());
        assertEquals(USER_FOLLOWING, updatedUser.getFollowingAmount().intValue());
        assertEquals(REVIEW_AMOUNT, updatedUser.getReviewAmount().intValue());
        assertEquals(PREFERRED_LANGUAGE, updatedUser.getPreferredLanguage());
        assertTrue(updatedUser.isModerator());
        assertTrue(updatedUser.isVerified());
        
        // Image validation
        assertArrayEquals(BYTES, updatedUser.getImage().getBytes());

        // check if user is saved correctly in database
        assertEquals(1, em.createQuery("SELECT COUNT(u) FROM User u " +
                                "JOIN u.image " +
                                "WHERE u.id = :userId " +
                                "AND u.username = :username " +
                                "AND u.email = :email " +
                                "AND u.password = :password " +
                                "AND u.name = :name " +
                                "AND u.bio = :bio " +
                                "AND u.followersAmount = :followersAmount " +
                                "AND u.followingAmount = :followingAmount " +
                                "AND u.reviewAmount = :reviewAmount " +
                                "AND u.verified = :verified " +
                                "AND u.moderator = :moderator " +
                                "AND u.preferredLanguage = :preferredLanguage " +
                                "AND u.image.id = :imageId",
                        Long.class)
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .setParameter("username", NEW_USERNAME)
                .setParameter("email", NEW_EMAIL)
                .setParameter("password", NEW_PASSWORD)
                .setParameter("name", NEW_NAME)
                .setParameter("bio", NEW_BIO)
                .setParameter("followersAmount", USER_FOLLOWERS)
                .setParameter("followingAmount", USER_FOLLOWING)
                .setParameter("reviewAmount", REVIEW_AMOUNT)
                .setParameter("verified", VERIFIED_TRUE)
                .setParameter("moderator", MODERATOR_TRUE)
                .setParameter("preferredLanguage", PREFERRED_LANGUAGE)
                .setParameter("imageId", NEW_IMAGE_ID)
                .getSingleResult().intValue());
    }

    @Test
    public void test_deleteById () {
        // 1. Pre-conditions - user exists

        // 2. Execute
        int rowsChanged = userDao.deleteById(PRE_EXISTING_USER_ID);

        // 3. Post-conditions
        assertEquals(1, rowsChanged);
        assertNull(em.find(User.class, PRE_EXISTING_USER_ID));
    }

    @Test
    public void test_deleteById_NoUser () {
        // 1. Pre-conditions - user does not exists

        // 2. Execute
        int rowsChanged = userDao.deleteById(NEW_USER_ID);

        // 3. Post-conditions
        assertEquals(0, rowsChanged);
        assertNull(em.find(User.class, NEW_USER_ID));
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
    public void test_findByUsernameContaining() {
        // 1. Pre-conditions - users exists with substring

        // 2. Execute
        List<User> userList = userDao.findByUsernameContaining(PRE_EXISTING_USERNAME, 1, 5);

        // 3. Post-conditions
        assertEquals(4, userList.size());
        for (User user : userList) {
            assertTrue(user.getUsername().contains(PRE_EXISTING_USERNAME));
        }
    }

    @Test
    public void test_create() {
        // 1. Pre-conditions - no user with username or email exists
        Image image = new Image(NEW_IMAGE_ID, BYTES);

        // 2. Execute
        Optional<User> optionalUser = userDao.create(NEW_USERNAME, NEW_EMAIL, NEW_PASSWORD, image);

        // 3. Post-conditions
        assertTrue(optionalUser.isPresent());
        User user = optionalUser.get();
        
        // Basic validations
        assertEquals(NEW_USERNAME, user.getUsername());
        assertEquals(NEW_EMAIL, user.getEmail());
        assertEquals(NEW_PASSWORD, user.getPassword());
        
        // Default values
        assertNull(user.getName());
        assertNull(user.getBio());
        assertEquals(0, user.getFollowersAmount().intValue());
        assertEquals(0, user.getFollowingAmount().intValue());
        assertEquals(0, user.getReviewAmount().intValue());
        assertFalse(user.isModerator());
        assertFalse(user.isVerified());
        assertEquals("es", user.getPreferredLanguage());

        // check if user is saved correctly in database
        assertEquals(1,em.createQuery("SELECT COUNT(u) FROM User u " +
                                "JOIN u.image " +
                                "WHERE u.username = :username " +
                                "AND u.email = :email " +
                                "AND u.password = :password " +
                                "AND u.followersAmount = 0 " +
                                "AND u.followingAmount = 0 " +
                                "AND u.verified = false " +
                                "AND u.moderator = false " +
                                "AND u.image.id = :imageId",
                        Long.class)
                .setParameter("username", NEW_USERNAME)
                .setParameter("email", NEW_EMAIL)
                .setParameter("password", NEW_PASSWORD)
                .setParameter("imageId", NEW_IMAGE_ID)
                .getSingleResult().intValue());
    }

    @Test
    public void test_create_duplicateUsername() {
        // 1. Pre-conditions - User with the same username exists
        Image image = new Image(NEW_IMAGE_ID, BYTES);

        // 2. Execute
        Optional<User> user = userDao.create(PRE_EXISTING_USERNAME, NEW_EMAIL, NEW_PASSWORD, image);

        // 3. Post-conditions
        try {
            assertEquals(1, em.createQuery("SELECT u FROM User u " +
                                    "WHERE u.username = :username",
                            User.class)
                    .setParameter("username", PRE_EXISTING_USERNAME)
                    .getResultList().size()
            );
        } catch (PersistenceException e) {
            assertTrue(e.getCause() instanceof ConstraintViolationException);
        }

    }

    @Test
    public void test_create_duplicateEmail() {
        // 1. Pre-conditions - User with the same email exists
        Image image = new Image(NEW_IMAGE_ID, BYTES);

        // 2. Execute
        Optional<User> user = userDao.create(NEW_USERNAME, PRE_EXISTING_EMAIL, NEW_PASSWORD, image);

        // 3. Post-conditions
        try {
            assertEquals(1, em.createQuery("SELECT u FROM User u " +
                                    "WHERE u.email = :email",
                            User.class)
                    .setParameter("email", PRE_EXISTING_EMAIL)
                    .getResultList().size()
            );
        } catch (PersistenceException e) {
            assertTrue(e.getCause() instanceof ConstraintViolationException);
        }
    }

    @Test
    public void test_createFollowing () {
        // 1. Pre-conditions - first user is not following second user
        User user1 = new User(PRE_EXISTING_USER_2_ID, PRE_EXISTING_USERNAME_2, PRE_EXISTING_EMAIL_2, PRE_EXISTING_PASSWORD);
        User user2 = new User(PRE_EXISTING_USER_ID, PRE_EXISTING_USERNAME, PRE_EXISTING_EMAIL, PRE_EXISTING_PASSWORD);

        // 2. Execute
        int rowsChanged = userDao.createFollowing(user1, user2);

        // 3. Post-conditions
        assertEquals(1, em.createQuery("SELECT COUNT(f) FROM User u " +
                                "JOIN u.followers f " +
                                "WHERE u.id = :userId AND f.id = :following",
                        Long.class)
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .setParameter("following", PRE_EXISTING_USER_2_ID)
                .getSingleResult().intValue()
        );
        assertEquals(1, rowsChanged);
    }

    @Test
    public void test_createFollowing_AlreadyFollowing () {
        // 1. Pre-conditions - first user is following second user
        User user1 = new User(PRE_EXISTING_USER_ID, PRE_EXISTING_USERNAME, PRE_EXISTING_EMAIL, PRE_EXISTING_PASSWORD);
        User user2 = new User(PRE_EXISTING_USER_2_ID, PRE_EXISTING_USERNAME_2, PRE_EXISTING_EMAIL_2, PRE_EXISTING_PASSWORD);

        // 2. Execute
        int rowsChanged = userDao.createFollowing(user1, user2);

        // 3. Post-conditions
        assertEquals(1, em.createQuery("SELECT COUNT(f) FROM User u " +
                                "JOIN u.followers f " +
                                "WHERE u.id = :userId AND f.id = :following",
                        Long.class)
                .setParameter("userId", PRE_EXISTING_USER_2_ID)
                .setParameter("following", PRE_EXISTING_USER_ID)
                .getSingleResult().intValue()
        );
        assertEquals(0, rowsChanged);
    }

    @Test
    public void test_countFollowers() {
        // 1. Pre-conditions - user exists

        // 2. Execute
        int followersAmount = userDao.countFollowers(PRE_EXISTING_USER_ID);

        // 3. Post-conditions
        assertEquals(3, followersAmount);
    }

    @Test
    public void test_countFollowing() {
        // 1. Pre-conditions - user exists

        // 2. Execute
        int followingAmount = userDao.countFollowing(PRE_EXISTING_USER_ID);

        // 3. Post-conditions
        assertEquals(1, followingAmount);
    }

    @Test
    public void test_undoFollowing () {
        // 1. Pre-conditions - first user is following the second user
        User user1 = new User(PRE_EXISTING_USER_ID, PRE_EXISTING_USERNAME, PRE_EXISTING_EMAIL, PRE_EXISTING_PASSWORD);
        User user2 = new User(PRE_EXISTING_USER_2_ID, PRE_EXISTING_USERNAME_2, PRE_EXISTING_EMAIL_2, PRE_EXISTING_PASSWORD);

        // 2. Execute
        int rowsChanged = userDao.undoFollowing(user1, user2);

        // 3. Post-conditions
        assertEquals(0, em.createQuery("SELECT COUNT(f) FROM User u " +
                                "Join u.followers f " +
                                "WHERE u.id = :userId AND f.id = :following",
                        Long.class)
                .setParameter("userId", PRE_EXISTING_USER_2_ID)
                .setParameter("following", PRE_EXISTING_USER_ID)
                .getSingleResult().intValue()
        );
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
        assertEquals(0, em.createQuery("SELECT COUNT(f) FROM User u " +
                                "Join u.followers f " +
                                "WHERE u.id = :userId AND f.id = :following",
                        Long.class)
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .setParameter("following", PRE_EXISTING_USER_2_ID)
                .getSingleResult().intValue()
        );
        assertEquals(0, rowsChanged);
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
    public void test_getFollowers () {
        // 1. Pre-conditions - user has 3 followers

        // 2. Execute
        List<User> userList = userDao.getFollowers(PRE_EXISTING_USER_ID, 1, 10);

        // 3. Post-conditions
        assertEquals(3, userList.size());
    }

    @Test
    public void test_getFollowing () {
        // 1. Pre-conditions - user is following 3 other users

        // 2. Execute
        List<User> userList = userDao.getFollowings(PRE_EXISTING_USER_2_ID, 1, 10);

        // 3. Post-conditions
        assertEquals(3, userList.size());
    }

    @Test
    public void test_updateUserReviewAmount () {
        // 1. Pre-conditions - user exists with 3 reviews

        // 2. Execute
        userDao.updateUserReviewAmount(PRE_EXISTING_USER_ID);

        // 3. Post-conditions
        assertEquals(2, em.createQuery("SELECT COUNT(r) FROM Review r " +
                                "JOIN r.user " +
                                "WHERE r.user.id = :userId " +
                                "AND r.isBlocked = false",
                        Long.class)
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .getSingleResult().intValue()
        );
    }

    //============================ FAVORITE ARTISTS ============================
    //----------------- Get ------------------------
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

    //----------------- Add ------------------------
    @Test
    public void test_addFavoriteArtist () {
        // 1. Pre-conditions - user exists and artist is not favorite

        // 2. Execute
        boolean updated = userDao.addFavoriteArtist(PRE_EXISTING_USER_ID, PRE_EXISTING_ARTIST_2_ID);

        // 3. Post-conditions
        assertTrue(updated);
        assertEquals(1, em.createQuery("SELECT COUNT(a) FROM User u " +
                        "JOIN u.favoriteArtists a " +
                        "WHERE u.id = :userId " +
                          "AND a.id = :artistId", Long.class)
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .setParameter("artistId", PRE_EXISTING_ARTIST_2_ID)
                .getSingleResult().intValue()
        );
    }

    @Test
    public void test_addFavoriteArtist_NoUser () {
        // 1. Pre-conditions - user does not exist

        // 2. Execute
        boolean updated = userDao.addFavoriteArtist(NEW_USER_ID, PRE_EXISTING_ARTIST_2_ID);

        // 3. Post-conditions
        assertFalse(updated);
        assertEquals(0, em.createQuery("SELECT COUNT(a) FROM User u " +
                        "JOIN u.favoriteArtists a " +
                        "WHERE u.id = :userId " +
                        "AND a.id = :artistId", Long.class)
                .setParameter("userId", NEW_USER_ID)
                .setParameter("artistId", PRE_EXISTING_ARTIST_2_ID)
                .getSingleResult().intValue()
        );
    }

    @Test
    public void test_addFavoriteArtist_NoArtist () {
        // 1. Pre-conditions - artist does not exist

        // 2. Execute
        boolean updated = userDao.addFavoriteArtist(PRE_EXISTING_USER_ID, NEW_ARTIST_ID);

        // 3. Post-conditions
        assertFalse(updated);
        assertEquals(0, em.createQuery("SELECT COUNT(a) FROM User u " +
                        "JOIN u.favoriteArtists a " +
                        "WHERE u.id = :userId " +
                        "AND a.id = :artistId", Long.class)
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .setParameter("artistId", NEW_ARTIST_ID)
                .getSingleResult().intValue()
        );
    }

    //----------------- Remove ------------------------
    @Test
    public void test_removeFavoriteArtist() {
        // 1. Pre-conditions - user exists and artist is favorite

        // 2. Execute
        boolean removed = userDao.removeFavoriteArtist(PRE_EXISTING_USER_ID, PRE_EXISTING_ARTIST_ID);

        // 3. Post-conditions
        assertTrue(removed);
        // Verify the specific relationship was removed
        assertEquals(0, em.createQuery("SELECT COUNT(a) FROM User u " +
                        "JOIN u.favoriteArtists a " +
                        "WHERE u.id = :userId " +
                        "AND a.id = :artistId", Long.class)
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .setParameter("artistId", PRE_EXISTING_ARTIST_ID)
                .getSingleResult().intValue()
        );
        // Verify artist still exists
        assertNotNull(em.find(Artist.class, PRE_EXISTING_ARTIST_ID));
        // Verify user still exists
        assertNotNull(em.find(User.class, PRE_EXISTING_USER_ID));
    }

    @Test
    public void test_removeFavoriteArtist_NoUser() {
        // 1. Pre-conditions - user does not exist

        // 2. Execute
        boolean removed = userDao.removeFavoriteArtist(NEW_USER_ID, PRE_EXISTING_ARTIST_ID);

        // 3. Post-conditions
        assertFalse(removed);
        assertEquals(0, em.createQuery("SELECT COUNT(a) FROM User u " +
                        "JOIN u.favoriteArtists a " +
                        "WHERE u.id = :userId " +
                        "AND a.id = :artistId", Long.class)
                .setParameter("userId", NEW_USER_ID)
                .setParameter("artistId", PRE_EXISTING_ARTIST_ID)
                .getSingleResult().intValue()
        );
    }

    @Test
    public void test_removeFavoriteArtist_NoArtist() {
        // 1. Pre-conditions - artist does not exist

        // 2. Execute
        boolean removed = userDao.removeFavoriteArtist(PRE_EXISTING_USER_ID, NEW_ARTIST_ID);

        // 3. Post-conditions
        assertFalse(removed);
        assertEquals(0, em.createQuery("SELECT COUNT(a) FROM User u " +
                        "JOIN u.favoriteArtists a " +
                        "WHERE u.id = :userId " +
                        "AND a.id = :artistId", Long.class)
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .setParameter("artistId", NEW_ARTIST_ID)
                .getSingleResult().intValue()
        );
    }

    @Test
    public void test_removeFavoriteArtist_NotFavorite() {
        // 1. Pre-conditions - artist is not in user's favorites

        // 2. Execute
        boolean removed = userDao.removeFavoriteArtist(PRE_EXISTING_USER_ID, PRE_EXISTING_ARTIST_2_ID);

        // 3. Post-conditions
        assertFalse(removed);
        assertEquals(0, em.createQuery("SELECT COUNT(a) FROM User u " +
                        "JOIN u.favoriteArtists a " +
                        "WHERE u.id = :userId " +
                        "AND a.id = :artistId", Long.class)
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .setParameter("artistId", PRE_EXISTING_ARTIST_2_ID)
                .getSingleResult().intValue()
        );
    }

    //----------------- Get Count ------------------------
    @Test
    public void test_getFavoriteArtistsCount() {
        // 1. Pre-conditions - user exists and has 1 favorite artist

        // 2. Execute
        int count = userDao.getFavoriteArtistsCount(PRE_EXISTING_USER_ID);

        // 3. Post-conditions
        assertEquals(1, count);
        assertEquals(1, em.createQuery("SELECT COUNT(a) FROM User u " +
                        "JOIN u.favoriteArtists a " +
                        "WHERE u.id = :userId", Long.class)
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .getSingleResult().intValue()
        );
    }

    @Test
    public void test_getFavoriteArtistsCount_NoUser() {
        // 1. Pre-conditions - user does not exist

        // 2. Execute
        int count = userDao.getFavoriteArtistsCount(NEW_USER_ID);

        // 3. Post-conditions
        assertEquals(0, count);
        assertEquals(0, em.createQuery("SELECT COUNT(a) FROM User u " +
                        "JOIN u.favoriteArtists a " +
                        "WHERE u.id = :userId", Long.class)
                .setParameter("userId", NEW_USER_ID)
                .getSingleResult().intValue()
        );
    }

    @Test
    public void test_getFavoriteArtistsCount_MultipleArtists() {
        // 1. Pre-conditions - user exists and has 5 favorite artists

        // 2. Execute
        int count = userDao.getFavoriteArtistsCount(PRE_EXISTING_USER_2_ID);

        // 3. Post-conditions
        assertEquals(5, count);
        // Verify the actual artists
        List<Artist> favorites = em.createQuery(
                "SELECT a FROM User u JOIN u.favoriteArtists a WHERE u.id = :userId ORDER BY a.id", 
                Artist.class)
                .setParameter("userId", PRE_EXISTING_USER_2_ID)
                .getResultList();
        
        assertEquals(5, favorites.size());
        assertEquals(PRE_EXISTING_ARTIST_ID + 1, favorites.get(0).getId().longValue());
        assertEquals(PRE_EXISTING_ARTIST_ID + 2, favorites.get(1).getId().longValue());
        assertEquals(PRE_EXISTING_ARTIST_ID + 3, favorites.get(2).getId().longValue());
        assertEquals(PRE_EXISTING_ARTIST_ID + 4, favorites.get(3).getId().longValue());
        assertEquals(PRE_EXISTING_ARTIST_ID + 5, favorites.get(4).getId().longValue());
    }

    //----------------- Is ------------------------
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

    //============================ FAVORITE ALBUMS ============================
    //----------------- Get ------------------------
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

    //----------------- Add ------------------------
    @Test
    public void test_addFavoriteAlbum () {
        // 1. Pre-conditions - user exists and album is not favorite yet

        // 2. Execute
        boolean updated = userDao.addFavoriteAlbum(PRE_EXISTING_USER_ID, PRE_EXISTING_ALBUM_2_ID);

        // 3. Post-conditions
        assertTrue(updated);
        assertEquals(1, em.createQuery("SELECT COUNT(a) FROM User u " +
                        "JOIN u.favoriteAlbums a " +
                        "WHERE u.id = :userId " +
                        "AND a.id = :albumId", Long.class)
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .setParameter("albumId", PRE_EXISTING_ALBUM_2_ID)
                .getSingleResult().intValue()
        );
    }

    @Test
    public void test_addFavoriteAlbum_NoUser () {
        // 1. Pre-conditions - user does not exist

        // 2. Execute
        boolean updated = userDao.addFavoriteAlbum(NEW_USER_ID, PRE_EXISTING_ALBUM_2_ID);

        // 3. Post-conditions
        assertFalse(updated);
        assertEquals(0, em.createQuery("SELECT COUNT(a) FROM User u " +
                        "JOIN u.favoriteAlbums a " +
                        "WHERE u.id = :userId " +
                        "AND a.id = :albumId", Long.class)
                .setParameter("userId", NEW_USER_ID)
                .setParameter("albumId", PRE_EXISTING_ALBUM_2_ID)
                .getSingleResult().intValue()
        );
    }

    @Test
    public void test_addFavoriteAlbum_NoAlbum () {
        // 1. Pre-conditions - Album does not exist

        // 2. Execute
        boolean updated = userDao.addFavoriteAlbum(PRE_EXISTING_USER_ID, NEW_ALBUM_ID);

        // 3. Post-conditions
        assertFalse(updated);
        assertEquals(0, em.createQuery("SELECT COUNT(a) FROM User u " +
                        "JOIN u.favoriteAlbums a " +
                        "WHERE u.id = :userId " +
                        "AND a.id = :albumId", Long.class)
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .setParameter("albumId", NEW_ALBUM_ID)
                .getSingleResult().intValue()
        );
    }

    //----------------- Remove ------------------------
    @Test
    public void test_removeFavoriteAlbum() {
        // 1. Pre-conditions - user exists and album is favorite

        // 2. Execute
        boolean removed = userDao.removeFavoriteAlbum(PRE_EXISTING_USER_ID, PRE_EXISTING_ALBUM_ID);

        // 3. Post-conditions
        assertTrue(removed);
        assertEquals(0, em.createQuery("SELECT COUNT(a) FROM User u " +
                        "JOIN u.favoriteAlbums a " +
                        "WHERE u.id = :userId " +
                        "AND a.id = :albumId", Long.class)
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .setParameter("albumId", PRE_EXISTING_ALBUM_ID)
                .getSingleResult().intValue()
        );
    }

    @Test
    public void test_removeFavoriteAlbum_NoUser() {
        // 1. Pre-conditions - user does not exist

        // 2. Execute
        boolean removed = userDao.removeFavoriteAlbum(NEW_USER_ID, PRE_EXISTING_ALBUM_ID);

        // 3. Post-conditions
        assertFalse(removed);
        assertEquals(0, em.createQuery("SELECT COUNT(a) FROM User u " +
                        "JOIN u.favoriteAlbums a " +
                        "WHERE u.id = :userId " +
                        "AND a.id = :albumId", Long.class)
                .setParameter("userId", NEW_USER_ID)
                .setParameter("albumId", PRE_EXISTING_ALBUM_ID)
                .getSingleResult().intValue()
        );
    }

    @Test
    public void test_removeFavoriteAlbum_NoAlbum() {
        // 1. Pre-conditions - album does not exist

        // 2. Execute
        boolean removed = userDao.removeFavoriteAlbum(PRE_EXISTING_USER_ID, NEW_ALBUM_ID);

        // 3. Post-conditions
        assertFalse(removed);
        assertEquals(0, em.createQuery("SELECT COUNT(a) FROM User u " +
                        "JOIN u.favoriteAlbums a " +
                        "WHERE u.id = :userId " +
                        "AND a.id = :albumId", Long.class)
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .setParameter("albumId", NEW_ALBUM_ID)
                .getSingleResult().intValue()
        );
    }

    @Test
    public void test_removeFavoriteAlbum_NotFavorite() {
        // 1. Pre-conditions - album is not in user's favorites

        // 2. Execute
        boolean removed = userDao.removeFavoriteAlbum(PRE_EXISTING_USER_ID, PRE_EXISTING_ALBUM_2_ID);

        // 3. Post-conditions
        assertFalse(removed);
        assertEquals(0, em.createQuery("SELECT COUNT(a) FROM User u " +
                        "JOIN u.favoriteAlbums a " +
                        "WHERE u.id = :userId " +
                        "AND a.id = :albumId", Long.class)
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .setParameter("albumId", PRE_EXISTING_ALBUM_2_ID)
                .getSingleResult().intValue()
        );
    }

    //----------------- Get Count ------------------------
    @Test
    public void test_getFavoriteAlbumsCount() {
        // 1. Pre-conditions - user exists and has 1 favorite album

        // 2. Execute
        int count = userDao.getFavoriteAlbumsCount(PRE_EXISTING_USER_ID);

        // 3. Post-conditions
        assertEquals(1, count);
        assertEquals(1, em.createQuery("SELECT COUNT(a) FROM User u " +
                        "JOIN u.favoriteAlbums a " +
                        "WHERE u.id = :userId", Long.class)
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .getSingleResult().intValue()
        );
    }

    @Test
    public void test_getFavoriteAlbumsCount_NoUser() {
        // 1. Pre-conditions - user does not exist

        // 2. Execute
        int count = userDao.getFavoriteAlbumsCount(NEW_USER_ID);

        // 3. Post-conditions
        assertEquals(0, count);
        assertEquals(0, em.createQuery("SELECT COUNT(a) FROM User u " +
                        "JOIN u.favoriteAlbums a " +
                        "WHERE u.id = :userId", Long.class)
                .setParameter("userId", NEW_USER_ID)
                .getSingleResult().intValue()
        );
    }

    @Test
    public void test_getFavoriteAlbumsCount_MultipleAlbums() {
        // 1. Pre-conditions - user exists and has 5 favorite albums

        // 2. Execute
        int count = userDao.getFavoriteAlbumsCount(PRE_EXISTING_USER_2_ID);

        // 3. Post-conditions
        assertEquals(5, count);
        assertEquals(5, em.createQuery("SELECT COUNT(a) FROM User u " +
                        "JOIN u.favoriteAlbums a " +
                        "WHERE u.id = :userId", Long.class)
                .setParameter("userId", PRE_EXISTING_USER_2_ID)
                .getSingleResult().intValue()
        );
    }

    //----------------- Is ------------------------
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

    //============================ FAVORITE SONGS ============================
    //----------------- Get ------------------------
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

    //----------------- Add ------------------------
    @Test
    public void test_addFavoriteSong () {
        // 1. Pre-conditions - user exists and song is not favorite yet

        // 2. Execute
        boolean updated = userDao.addFavoriteSong(PRE_EXISTING_USER_ID, PRE_EXISTING_SONG_2_ID);

        // 3. Post-conditions
        assertTrue(updated);
        assertEquals(1, em.createQuery("SELECT COUNT(s) FROM User u " +
                        "JOIN u.favoriteSongs s " +
                        "WHERE u.id = :userId " +
                        "AND s.id = :songId", Long.class)
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .setParameter("songId", PRE_EXISTING_SONG_2_ID)
                .getSingleResult().intValue()
        );
    }

    @Test
    public void test_addFavoriteSong_NoUser () {
        // 1. Pre-conditions - user does not exist

        // 2. Execute
        boolean updated = userDao.addFavoriteSong(NEW_USER_ID, PRE_EXISTING_SONG_2_ID);

        // 3. Post-conditions
        assertFalse(updated);
        assertEquals(0, em.createQuery("SELECT COUNT(s) FROM User u " +
                        "JOIN u.favoriteSongs s " +
                        "WHERE u.id = :userId " +
                        "AND s.id = :songId", Long.class)
                .setParameter("userId", NEW_USER_ID)
                .setParameter("songId", PRE_EXISTING_SONG_2_ID)
                .getSingleResult().intValue()
        );
    }

    @Test
    public void test_addFavoriteSong_NoSong () {
        // 1. Pre-conditions - Song does not exist

        // 2. Execute
        boolean updated = userDao.addFavoriteSong(PRE_EXISTING_USER_ID, NEW_SONG_ID);

        // 3. Post-conditions
        assertFalse(updated);
        assertEquals(0, em.createQuery("SELECT COUNT(s) FROM User u " +
                        "JOIN u.favoriteSongs s " +
                        "WHERE u.id = :userId " +
                        "AND s.id = :songId", Long.class)
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .setParameter("songId", NEW_SONG_ID)
                .getSingleResult().intValue()
        );
    }

    //----------------- Remove ------------------------
    @Test
    public void test_removeFavoriteSong() {
        // 1. Pre-conditions - user exists and song is favorite

        // 2. Execute
        boolean removed = userDao.removeFavoriteSong(PRE_EXISTING_USER_ID, PRE_EXISTING_SONG_ID);

        // 3. Post-conditions
        assertTrue(removed);
        assertEquals(0, em.createQuery("SELECT COUNT(s) FROM User u " +
                        "JOIN u.favoriteSongs s " +
                        "WHERE u.id = :userId " +
                        "AND s.id = :songId", Long.class)
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .setParameter("songId", PRE_EXISTING_SONG_ID)
                .getSingleResult().intValue()
        );
    }

    @Test
    public void test_removeFavoriteSong_NoUser() {
        // 1. Pre-conditions - user does not exist

        // 2. Execute
        boolean removed = userDao.removeFavoriteSong(NEW_USER_ID, PRE_EXISTING_SONG_ID);

        // 3. Post-conditions
        assertFalse(removed);
        assertEquals(0, em.createQuery("SELECT COUNT(s) FROM User u " +
                        "JOIN u.favoriteSongs s " +
                        "WHERE u.id = :userId " +
                        "AND s.id = :songId", Long.class)
                .setParameter("userId", NEW_USER_ID)
                .setParameter("songId", PRE_EXISTING_SONG_ID)
                .getSingleResult().intValue()
        );
    }

    @Test
    public void test_removeFavoriteSong_NoSong() {
        // 1. Pre-conditions - song does not exist

        // 2. Execute
        boolean removed = userDao.removeFavoriteSong(PRE_EXISTING_USER_ID, NEW_SONG_ID);

        // 3. Post-conditions
        assertFalse(removed);
        assertEquals(0, em.createQuery("SELECT COUNT(s) FROM User u " +
                        "JOIN u.favoriteSongs s " +
                        "WHERE u.id = :userId " +
                        "AND s.id = :songId", Long.class)
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .setParameter("songId", NEW_SONG_ID)
                .getSingleResult().intValue()
        );
    }

    @Test
    public void test_removeFavoriteSong_NotFavorite() {
        // 1. Pre-conditions - song is not in user's favorites

        // 2. Execute
        boolean removed = userDao.removeFavoriteSong(PRE_EXISTING_USER_ID, PRE_EXISTING_SONG_2_ID);

        // 3. Post-conditions
        assertFalse(removed);
        assertEquals(0, em.createQuery("SELECT COUNT(s) FROM User u " +
                        "JOIN u.favoriteSongs s " +
                        "WHERE u.id = :userId " +
                        "AND s.id = :songId", Long.class)
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .setParameter("songId", PRE_EXISTING_SONG_2_ID)
                .getSingleResult().intValue()
        );
    }

    //----------------- Get Count ------------------------
    @Test
    public void test_getFavoriteSongsCount() {
        // 1. Pre-conditions - user exists and has 1 favorite song

        // 2. Execute
        int count = userDao.getFavoriteSongsCount(PRE_EXISTING_USER_ID);

        // 3. Post-conditions
        assertEquals(1, count);
        assertEquals(1, em.createQuery("SELECT COUNT(s) FROM User u " +
                        "JOIN u.favoriteSongs s " +
                        "WHERE u.id = :userId", Long.class)
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .getSingleResult().intValue()
        );
    }

    @Test
    public void test_getFavoriteSongsCount_NoUser() {
        // 1. Pre-conditions - user does not exist

        // 2. Execute
        int count = userDao.getFavoriteSongsCount(NEW_USER_ID);

        // 3. Post-conditions
        assertEquals(0, count);
        assertEquals(0, em.createQuery("SELECT COUNT(s) FROM User u " +
                        "JOIN u.favoriteSongs s " +
                        "WHERE u.id = :userId", Long.class)
                .setParameter("userId", NEW_USER_ID)
                .getSingleResult().intValue()
        );
    }

    @Test
    public void test_getFavoriteSongsCount_MultipleSongs() {
        // 1. Pre-conditions - user exists and has 5 favorite songs

        // 2. Execute
        int count = userDao.getFavoriteSongsCount(PRE_EXISTING_USER_2_ID);

        // 3. Post-conditions
        assertEquals(5, count);
        assertEquals(5, em.createQuery("SELECT COUNT(s) FROM User u " +
                        "JOIN u.favoriteSongs s " +
                        "WHERE u.id = :userId", Long.class)
                .setParameter("userId", PRE_EXISTING_USER_2_ID)
                .getSingleResult().intValue()
        );
    }

    //----------------- Is ------------------------
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
}
