package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.persistence.UserVerificationDao;
import ar.edu.itba.paw.exception.conflict.UserAlreadyExistsException;
import ar.edu.itba.paw.exception.not_found.UserNotFoundException;
import ar.edu.itba.paw.exception.email.VerificationEmailException;
import ar.edu.itba.paw.exception.conflict.FavoriteLimitException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.MessagingException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;
    private static final String USERNAME = "testuser";
    private static final String EMAIL = "test@test.com";
    private static final String PASSWORD = "password123";
    private static final String HASHED_PASSWORD = "hashed_password_123";
    private static final Long ARTIST_ID = 10L;
    private static final Long ALBUM_ID = 20L;
    private static final Long SONG_ID = 30L;
    private static final Long DEFAULT_PROFILE_IMG_ID = 2L;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserDao userDao;

    @Mock
    private UserVerificationDao userVerificationDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ImageService imageService;

    private User testUser;
    private User otherUser;
    private Image defaultImage;

    @Before
    public void setUp() {
        defaultImage = new Image(DEFAULT_PROFILE_IMG_ID, new byte[]{});
        testUser = new User(USER_ID, USERNAME, EMAIL, HASHED_PASSWORD);
        testUser.setImage(defaultImage);
        otherUser = new User(OTHER_USER_ID, "otheruser", "other@test.com", HASHED_PASSWORD);
    }

    // ========== CREATE TESTS ==========

    @Test
    public void test_create_successfulUserCreation() {
        // 1. Pre-conditions
        Mockito.when(passwordEncoder.encode(PASSWORD)).thenReturn(HASHED_PASSWORD);
        Mockito.when(userDao.findByUsername(USERNAME)).thenReturn(Optional.empty());
        Mockito.when(userDao.findByEmail(EMAIL)).thenReturn(Optional.empty());
        Mockito.when(imageService.getDefaultProfileImgId()).thenReturn(DEFAULT_PROFILE_IMG_ID);
        Mockito.when(imageService.findById(DEFAULT_PROFILE_IMG_ID)).thenReturn(defaultImage);
        Mockito.when(userDao.create(eq(USERNAME), eq(EMAIL), eq(HASHED_PASSWORD), eq(defaultImage)))
                .thenReturn(Optional.of(testUser));

        // 2. Execute
        User created = userService.create(USERNAME, EMAIL, PASSWORD);

        // 3. Post-conditions
        assertNotNull(created);
        assertEquals(USERNAME, created.getUsername());
        assertEquals(EMAIL, created.getEmail());

        Mockito.verify(passwordEncoder).encode(PASSWORD);
        Mockito.verify(userDao).create(eq(USERNAME), eq(EMAIL), eq(HASHED_PASSWORD), eq(defaultImage));
        Mockito.verify(userVerificationDao).startVerification(
                eq(VerificationType.VERIFY_EMAIL),
                eq(testUser),
                anyString()
        );
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void test_create_usernameAlreadyExists() {
        // 1. Pre-conditions
        Mockito.when(userDao.findByUsername(USERNAME)).thenReturn(Optional.of(testUser));

        // 2. Execute
        userService.create(USERNAME, EMAIL, PASSWORD);

        // 3. Post-conditions - exception thrown
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void test_create_emailAlreadyExists() {
        // 1. Pre-conditions
        Mockito.when(userDao.findByUsername(USERNAME)).thenReturn(Optional.empty());
        Mockito.when(userDao.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));

        // 2. Execute
        userService.create(USERNAME, EMAIL, PASSWORD);

        // 3. Post-conditions - exception thrown
    }

    // ========== FIND TESTS ==========

    @Test
    public void test_findUserById_successfulFind() {
        // 1. Pre-conditions
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(testUser));
        Mockito.when(userDao.isFollowing(OTHER_USER_ID, USER_ID)).thenReturn(false);

        // 2. Execute
        User found = userService.findUserById(USER_ID, OTHER_USER_ID);

        // 3. Post-conditions
        assertNotNull(found);
        assertEquals(USER_ID, found.getId());
        assertEquals(USERNAME, found.getUsername());
        assertFalse(found.getIsFollowed());
    }

    @Test(expected = UserNotFoundException.class)
    public void test_findUserById_userNotFound() {
        // 1. Pre-conditions
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        // 2. Execute
        userService.findUserById(USER_ID, null);

        // 3. Post-conditions - exception thrown
    }

    @Test
    public void test_findByEmail_successful() {
        // 1. Pre-conditions
        Mockito.when(userDao.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));

        // 2. Execute
        User found = userService.findByEmail(EMAIL);

        // 3. Post-conditions
        assertNotNull(found);
        assertEquals(EMAIL, found.getEmail());
    }

    @Test(expected = UserNotFoundException.class)
    public void test_findByEmail_notFound() {
        // 1. Pre-conditions
        Mockito.when(userDao.findByEmail(EMAIL)).thenReturn(Optional.empty());

        // 2. Execute
        userService.findByEmail(EMAIL);

        // 3. Post-conditions - exception thrown
    }

    @Test
    public void test_findByUsername_successful() {
        // 1. Pre-conditions
        Mockito.when(userDao.findByUsername(USERNAME)).thenReturn(Optional.of(testUser));

        // 2. Execute
        User found = userService.findByUsername(USERNAME);

        // 3. Post-conditions
        assertNotNull(found);
        assertEquals(USERNAME, found.getUsername());
    }

    @Test(expected = UserNotFoundException.class)
    public void test_findByUsername_notFound() {
        // 1. Pre-conditions
        Mockito.when(userDao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        // 2. Execute
        userService.findByUsername(USERNAME);

        // 3. Post-conditions - exception thrown
    }

    @Test
    public void test_usernameExists_existingUsername() {
        // 1. Pre-conditions
        Mockito.when(userDao.findByUsername(USERNAME)).thenReturn(Optional.of(testUser));

        // 2. Execute
        Boolean exists = userService.usernameExists(USERNAME);

        // 3. Post-conditions
        assertTrue(exists);
    }

    @Test
    public void test_usernameExists_nonExistingUsername() {
        // 1. Pre-conditions
        Mockito.when(userDao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        // 2. Execute
        Boolean exists = userService.usernameExists(USERNAME);

        // 3. Post-conditions
        assertFalse(exists);
    }

    @Test
    public void test_emailExists_existingEmail() {
        // 1. Pre-conditions
        Mockito.when(userDao.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));

        // 2. Execute
        Boolean exists = userService.emailExists(EMAIL);

        // 3. Post-conditions
        assertTrue(exists);
    }

    @Test
    public void test_emailExists_nonExistingEmail() {
        // 1. Pre-conditions
        Mockito.when(userDao.findByEmail(EMAIL)).thenReturn(Optional.empty());

        // 2. Execute
        Boolean exists = userService.emailExists(EMAIL);

        // 3. Post-conditions
        assertFalse(exists);
    }

    // ========== PASSWORD TESTS ==========

    @Test
    public void test_changePassword_successful() {
        // 1. Pre-conditions
        String newPassword = "newPassword456";
        String newHashed = "new_hashed_password";

        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(testUser));
        Mockito.when(passwordEncoder.encode(newPassword)).thenReturn(newHashed);

        // 2. Execute
        Boolean result = userService.changePassword(USER_ID, newPassword);

        // 3. Post-conditions
        assertTrue(result);
        assertEquals(newHashed, testUser.getPassword());
        Mockito.verify(passwordEncoder).encode(newPassword);
    }

    @Test(expected = UserNotFoundException.class)
    public void test_changePassword_userNotFound() {
        // 1. Pre-conditions
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        // 2. Execute
        userService.changePassword(USER_ID, "newPassword");

        // 3. Post-conditions - exception thrown
    }

    // ========== FOLLOWING TESTS ==========

    @Test
    public void test_createFollowing_successful() {
        // 1. Pre-conditions
        Mockito.when(userDao.isFollowing(USER_ID, OTHER_USER_ID)).thenReturn(false);
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(testUser));
        Mockito.when(userDao.findById(OTHER_USER_ID)).thenReturn(Optional.of(otherUser));
        Mockito.when(userDao.createFollowing(testUser, otherUser)).thenReturn(1);

        // 2. Execute
        Integer result = userService.createFollowing(USER_ID, OTHER_USER_ID);

        // 3. Post-conditions
        assertEquals(Integer.valueOf(1), result);
        Mockito.verify(userDao).createFollowing(testUser, otherUser);
        Mockito.verify(notificationService).notifyFollow(otherUser, testUser);
    }

    @Test
    public void test_createFollowing_alreadyFollowing() {
        // 1. Pre-conditions
        Mockito.when(userDao.isFollowing(USER_ID, OTHER_USER_ID)).thenReturn(true);

        // 2. Execute
        Integer result = userService.createFollowing(USER_ID, OTHER_USER_ID);

        // 3. Post-conditions
        assertEquals(Integer.valueOf(0), result);
        Mockito.verify(userDao, Mockito.never()).createFollowing(any(), any());
        Mockito.verify(notificationService, Mockito.never()).notifyFollow(any(), any());
    }

    @Test(expected = UserNotFoundException.class)
    public void test_createFollowing_userNotFound() {
        // 1. Pre-conditions
        Mockito.when(userDao.isFollowing(USER_ID, OTHER_USER_ID)).thenReturn(false);
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        // 2. Execute
        userService.createFollowing(USER_ID, OTHER_USER_ID);

        // 3. Post-conditions - exception thrown
    }

    @Test(expected = UserNotFoundException.class)
    public void test_createFollowing_followingUserNotFound() {
        // 1. Pre-conditions
        Mockito.when(userDao.isFollowing(USER_ID, OTHER_USER_ID)).thenReturn(false);
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(testUser));
        Mockito.when(userDao.findById(OTHER_USER_ID)).thenReturn(Optional.empty());

        // 2. Execute
        userService.createFollowing(USER_ID, OTHER_USER_ID);

        // 3. Post-conditions - exception thrown
    }

    @Test
    public void test_undoFollowing_successful() {
        // 1. Pre-conditions
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(testUser));
        Mockito.when(userDao.findById(OTHER_USER_ID)).thenReturn(Optional.of(otherUser));
        Mockito.when(userDao.undoFollowing(testUser, otherUser)).thenReturn(1);

        // 2. Execute
        Integer result = userService.undoFollowing(USER_ID, OTHER_USER_ID);

        // 3. Post-conditions
        assertEquals(Integer.valueOf(1), result);
        Mockito.verify(userDao).undoFollowing(testUser, otherUser);
    }

    @Test
    public void test_isFollowing_returnsTrue() {
        // 1. Pre-conditions
        Mockito.when(userDao.isFollowing(USER_ID, OTHER_USER_ID)).thenReturn(true);

        // 2. Execute
        Boolean result = userService.isFollowing(USER_ID, OTHER_USER_ID);

        // 3. Post-conditions
        assertTrue(result);
    }

    @Test
    public void test_isFollowing_returnsFalse() {
        // 1. Pre-conditions
        Mockito.when(userDao.isFollowing(USER_ID, OTHER_USER_ID)).thenReturn(false);

        // 2. Execute
        Boolean result = userService.isFollowing(USER_ID, OTHER_USER_ID);

        // 3. Post-conditions
        assertFalse(result);
    }

    @Test
    public void test_isFollowing_nullUserIds() {
        // 1. Pre-conditions - none

        // 2. Execute
        Boolean result = userService.isFollowing(null, null);

        // 3. Post-conditions
        assertFalse(result);
        Mockito.verify(userDao, Mockito.never()).isFollowing(any(), any());
    }

    @Test
    public void test_getFollowers_successful() {
        // 1. Pre-conditions
        List<User> followers = Arrays.asList(otherUser);
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(testUser));
        Mockito.when(userDao.getFollowers(USER_ID, 1, 10)).thenReturn(followers);

        // 2. Execute
        List<User> result = userService.getFollowers(USER_ID, 1, 10);

        // 3. Post-conditions
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(otherUser, result.get(0));
    }

    @Test(expected = UserNotFoundException.class)
    public void test_getFollowers_userNotFound() {
        // 1. Pre-conditions
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        // 2. Execute
        userService.getFollowers(USER_ID, 1, 10);

        // 3. Post-conditions - exception thrown
    }

    @Test
    public void test_getFollowings_successful() {
        // 1. Pre-conditions
        List<User> followings = Arrays.asList(otherUser);
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(testUser));
        Mockito.when(userDao.getFollowings(USER_ID, 1, 10)).thenReturn(followings);

        // 2. Execute
        List<User> result = userService.getFollowings(USER_ID, 1, 10);

        // 3. Post-conditions
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(otherUser, result.get(0));
    }

    // ========== FAVORITE ARTIST TESTS ==========

    @Test
    public void test_addFavoriteArtist_successful() {
        // 1. Pre-conditions
        Mockito.when(userDao.getFavoriteArtistsCount(USER_ID)).thenReturn(3);
        Mockito.when(userDao.addFavoriteArtist(USER_ID, ARTIST_ID)).thenReturn(true);

        // 2. Execute
        Boolean result = userService.addFavoriteArtist(USER_ID, ARTIST_ID);

        // 3. Post-conditions
        assertTrue(result);
        Mockito.verify(userDao).addFavoriteArtist(USER_ID, ARTIST_ID);
    }

    @Test(expected = FavoriteLimitException.class)
    public void test_addFavoriteArtist_limitReached() {
        // 1. Pre-conditions
        Mockito.when(userDao.getFavoriteArtistsCount(USER_ID)).thenReturn(5);

        // 2. Execute
        userService.addFavoriteArtist(USER_ID, ARTIST_ID);

        // 3. Post-conditions - exception thrown
    }

    @Test
    public void test_removeFavoriteArtist_successful() {
        // 1. Pre-conditions
        Mockito.when(userDao.removeFavoriteArtist(USER_ID, ARTIST_ID)).thenReturn(true);

        // 2. Execute
        Boolean result = userService.removeFavoriteArtist(USER_ID, ARTIST_ID);

        // 3. Post-conditions
        assertTrue(result);
        Mockito.verify(userDao).removeFavoriteArtist(USER_ID, ARTIST_ID);
    }

    @Test
    public void test_isArtistFavorite_returnsTrue() {
        // 1. Pre-conditions
        Mockito.when(userDao.isArtistFavorite(USER_ID, ARTIST_ID)).thenReturn(true);

        // 2. Execute
        Boolean result = userService.isArtistFavorite(USER_ID, ARTIST_ID);

        // 3. Post-conditions
        assertTrue(result);
    }

    @Test
    public void test_getFavoriteArtists_successful() {
        // 1. Pre-conditions
        Artist artist = new Artist(ARTIST_ID, "Test Artist", "Bio", null);
        List<Artist> favorites = Arrays.asList(artist);
        Mockito.when(userDao.getFavoriteArtists(USER_ID)).thenReturn(favorites);

        // 2. Execute
        List<Artist> result = userService.getFavoriteArtists(USER_ID);

        // 3. Post-conditions
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(artist, result.get(0));
    }

    // ========== FAVORITE ALBUM TESTS ==========

    @Test
    public void test_addFavoriteAlbum_successful() {
        // 1. Pre-conditions
        Mockito.when(userDao.getFavoriteAlbumsCount(USER_ID)).thenReturn(2);
        Mockito.when(userDao.addFavoriteAlbum(USER_ID, ALBUM_ID)).thenReturn(true);

        // 2. Execute
        Boolean result = userService.addFavoriteAlbum(USER_ID, ALBUM_ID);

        // 3. Post-conditions
        assertTrue(result);
        Mockito.verify(userDao).addFavoriteAlbum(USER_ID, ALBUM_ID);
    }

    @Test(expected = FavoriteLimitException.class)
    public void test_addFavoriteAlbum_limitReached() {
        // 1. Pre-conditions
        Mockito.when(userDao.getFavoriteAlbumsCount(USER_ID)).thenReturn(5);

        // 2. Execute
        userService.addFavoriteAlbum(USER_ID, ALBUM_ID);

        // 3. Post-conditions - exception thrown
    }

    @Test
    public void test_removeFavoriteAlbum_successful() {
        // 1. Pre-conditions
        Mockito.when(userDao.removeFavoriteAlbum(USER_ID, ALBUM_ID)).thenReturn(true);

        // 2. Execute
        Boolean result = userService.removeFavoriteAlbum(USER_ID, ALBUM_ID);

        // 3. Post-conditions
        assertTrue(result);
    }

    // ========== FAVORITE SONG TESTS ==========

    @Test
    public void test_addFavoriteSong_successful() {
        // 1. Pre-conditions
        Mockito.when(userDao.getFavoriteSongsCount(USER_ID)).thenReturn(1);
        Mockito.when(userDao.addFavoriteSong(USER_ID, SONG_ID)).thenReturn(true);

        // 2. Execute
        Boolean result = userService.addFavoriteSong(USER_ID, SONG_ID);

        // 3. Post-conditions
        assertTrue(result);
        Mockito.verify(userDao).addFavoriteSong(USER_ID, SONG_ID);
    }

    @Test(expected = FavoriteLimitException.class)
    public void test_addFavoriteSong_limitReached() {
        // 1. Pre-conditions
        Mockito.when(userDao.getFavoriteSongsCount(USER_ID)).thenReturn(5);

        // 2. Execute
        userService.addFavoriteSong(USER_ID, SONG_ID);

        // 3. Post-conditions - exception thrown
    }

    @Test
    public void test_removeFavoriteSong_successful() {
        // 1. Pre-conditions
        Mockito.when(userDao.removeFavoriteSong(USER_ID, SONG_ID)).thenReturn(true);

        // 2. Execute
        Boolean result = userService.removeFavoriteSong(USER_ID, SONG_ID);

        // 3. Post-conditions
        assertTrue(result);
    }

    // ========== VERIFICATION TESTS ==========

    @Test
    public void test_verify_successful() {
        // 1. Pre-conditions
        String code = "verification-code-123";
        Mockito.when(userVerificationDao.verify(VerificationType.VERIFY_EMAIL, code))
                .thenReturn(USER_ID);

        // 2. Execute
        Long result = userService.verify(VerificationType.VERIFY_EMAIL, code);

        // 3. Post-conditions
        assertNotNull(result);
        assertEquals(USER_ID, result);
    }

    @Test
    public void test_verify_invalidCode() {
        // 1. Pre-conditions
        String code = "invalid-code";
        Mockito.when(userVerificationDao.verify(VerificationType.VERIFY_EMAIL, code))
                .thenReturn(null);

        // 2. Execute
        Long result = userService.verify(VerificationType.VERIFY_EMAIL, code);

        // 3. Post-conditions
        assertNull(result);
    }

    @Test
    public void test_createVerification_successful() throws MessagingException {
        // 1. Pre-conditions
        Mockito.doNothing().when(emailService).sendVerification(
                eq(VerificationType.VERIFY_EMAIL),
                eq(testUser),
                anyString()
        );

        // 2. Execute
        userService.createVerification(VerificationType.VERIFY_EMAIL, testUser);

        // 3. Post-conditions
        Mockito.verify(userVerificationDao).startVerification(
                eq(VerificationType.VERIFY_EMAIL),
                eq(testUser),
                anyString()
        );
        Mockito.verify(emailService).sendVerification(
                eq(VerificationType.VERIFY_EMAIL),
                eq(testUser),
                anyString()
        );
    }

    @Test(expected = VerificationEmailException.class)
    public void test_createVerification_emailFailure() throws MessagingException {
        // 1. Pre-conditions
        Mockito.doThrow(new MessagingException("Email failed"))
                .when(emailService).sendVerification(
                        eq(VerificationType.VERIFY_EMAIL),
                        eq(testUser),
                        anyString()
                );

        // 2. Execute
        userService.createVerification(VerificationType.VERIFY_EMAIL, testUser);

        // 3. Post-conditions - exception thrown
    }

    // ========== UPDATE TESTS ==========

    @Test
    public void test_updateUser_successful() {
        // 1. Pre-conditions
        User userUpdate = new User();
        userUpdate.setId(USER_ID);
        userUpdate.setBio("New bio");

        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(testUser));
        Mockito.when(userDao.updateUser(eq(USER_ID), any(User.class)))
                .thenReturn(Optional.of(testUser));

        // 2. Execute
        User result = userService.updateUser(userUpdate);

        // 3. Post-conditions
        assertNotNull(result);
        Mockito.verify(userDao).updateUser(eq(USER_ID), any(User.class));
    }

    @Test(expected = UserNotFoundException.class)
    public void test_updateUser_userNotFound() {
        // 1. Pre-conditions
        User userUpdate = new User();
        userUpdate.setId(USER_ID);

        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        // 2. Execute
        userService.updateUser(userUpdate);

        // 3. Post-conditions - exception thrown
    }

    @Test
    public void test_updateUserReviewAmount_successful() {
        // 1. Pre-conditions - none

        // 2. Execute
        userService.updateUserReviewAmount(USER_ID);

        // 3. Post-conditions
        Mockito.verify(userDao).updateUserReviewAmount(USER_ID);
    }

    // ========== DELETE TESTS ==========

    @Test
    public void test_deleteById_successful() {
        // 1. Pre-conditions
        Mockito.when(userDao.deleteById(USER_ID)).thenReturn(1);

        // 2. Execute
        Integer result = userService.deleteById(USER_ID);

        // 3. Post-conditions
        assertEquals(Integer.valueOf(1), result);
        Mockito.verify(userDao).deleteById(USER_ID);
    }

    @Test(expected = UserNotFoundException.class)
    public void test_deleteById_userNotFound() {
        // 1. Pre-conditions
        Mockito.when(userDao.deleteById(USER_ID)).thenReturn(0);

        // 2. Execute
        userService.deleteById(USER_ID);

        // 3. Post-conditions - exception thrown
    }

    // ========== COUNT TESTS ==========

    @Test
    public void test_countUsers_successful() {
        // 1. Pre-conditions
        Mockito.when(userDao.countUsers()).thenReturn(100L);

        // 2. Execute
        Long count = userService.countUsers();

        // 3. Post-conditions
        assertEquals(Long.valueOf(100), count);
    }

    // ========== RECOMMENDED USERS TESTS ==========

    @Test
    public void test_getRecommendedUsers_successful() {
        // 1. Pre-conditions
        List<User> recommended = Arrays.asList(otherUser);
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(testUser));
        Mockito.when(userDao.getRecommendedUsers(USER_ID, 1, 10)).thenReturn(recommended);

        // 2. Execute
        List<User> result = userService.getRecommendedUsers(USER_ID, 1, 10);

        // 3. Post-conditions
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test(expected = UserNotFoundException.class)
    public void test_getRecommendedUsers_userNotFound() {
        // 1. Pre-conditions
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        // 2. Execute
        userService.getRecommendedUsers(USER_ID, 1, 10);

        // 3. Post-conditions - exception thrown
    }

    // ========== CONTEXT DEPENDENT FIELDS TESTS ==========

    @Test
    public void test_setContextDependentFields_withLoggedUser() {
        // 1. Pre-conditions
        Mockito.when(userDao.isFollowing(OTHER_USER_ID, USER_ID)).thenReturn(true);

        // 2. Execute
        userService.setContextDependentFields(testUser, OTHER_USER_ID);

        // 3. Post-conditions
        assertTrue(testUser.getIsFollowed());
    }

    @Test
    public void test_setContextDependentFields_withoutLoggedUser() {
        // 1. Pre-conditions - none

        // 2. Execute
        userService.setContextDependentFields(testUser, null);

        // 3. Post-conditions
        assertFalse(testUser.getIsFollowed());
    }

    // ========== SEARCH TESTS ==========

    @Test
    public void test_findByUsernameContaining_successful() {
        // 1. Pre-conditions
        List<User> users = Arrays.asList(testUser, otherUser);
        Mockito.when(userDao.findByUsernameContaining("test", 1, 10)).thenReturn(users);

        // 2. Execute
        List<User> result = userService.findByUsernameContaining("test", 1, 10);

        // 3. Post-conditions
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void test_findAll_successful() {
        // 1. Pre-conditions
        List<User> allUsers = Arrays.asList(testUser, otherUser);
        Mockito.when(userDao.findAll()).thenReturn(allUsers);

        // 2. Execute
        List<User> result = userService.findAll();

        // 3. Post-conditions
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
