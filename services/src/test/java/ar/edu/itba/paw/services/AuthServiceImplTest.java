package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.AuthResult;
import ar.edu.itba.paw.models.RefreshToken;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.RefreshTokenDao;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.exception.not_found.UserNotFoundException;
import ar.edu.itba.paw.exception.InvalidTokenException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final String USERNAME = "testuser";
    private static final String EMAIL = "test@test.com";
    private static final String PASSWORD = "password123";
    private static final String HASHED_PASSWORD = "hashed_password_123";
    private static final String ACCESS_TOKEN = "access_token_jwt";
    private static final String REFRESH_TOKEN = "refresh_token_jwt";
    private static final String HASHED_REFRESH_TOKEN = "hashed_refresh_token";

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserService userService;

    @Mock
    private UserDao userDao;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenDao refreshTokenDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Captor
    private ArgumentCaptor<RefreshToken> refreshTokenCaptor;

    private User testUser;
    private RefreshToken refreshTokenEntity;

    @Before
    public void setUp() {
        testUser = new User(USER_ID, USERNAME, EMAIL, HASHED_PASSWORD);
        testUser.setModerator(false);
        refreshTokenEntity = new RefreshToken(HASHED_REFRESH_TOKEN, USER_ID,
                LocalDateTime.now().plusDays(30));
    }

    // ========== LOGIN TESTS ==========

    @Test
    public void test_login_successfulAuthentication() {
        // 1. Pre-conditions
        Mockito.when(userDao.findByUsername(USERNAME)).thenReturn(Optional.of(testUser));
        Mockito.when(passwordEncoder.matches(PASSWORD, HASHED_PASSWORD)).thenReturn(true);
        Mockito.when(jwtService.generateAccessToken(USER_ID, USERNAME, false))
                .thenReturn(ACCESS_TOKEN);
        Mockito.when(jwtService.generateRefreshToken(USER_ID, USERNAME))
                .thenReturn(REFRESH_TOKEN);

        // 2. Execute
        AuthResult result = authService.login(USERNAME, PASSWORD);

        // 3. Post-conditions
        assertNotNull(result);
        assertEquals(ACCESS_TOKEN, result.getAccessToken());
        assertEquals(REFRESH_TOKEN, result.getRefreshToken());
        assertEquals(testUser, result.getUser());

        Mockito.verify(passwordEncoder).matches(PASSWORD, HASHED_PASSWORD);
        Mockito.verify(jwtService).generateAccessToken(USER_ID, USERNAME, false);
        Mockito.verify(jwtService).generateRefreshToken(USER_ID, USERNAME);
        Mockito.verify(refreshTokenDao).save(any(RefreshToken.class));
    }

    @Test
    public void test_login_moderatorRole() {
        // 1. Pre-conditions
        testUser.setModerator(true);
        Mockito.when(userDao.findByUsername(USERNAME)).thenReturn(Optional.of(testUser));
        Mockito.when(passwordEncoder.matches(PASSWORD, HASHED_PASSWORD)).thenReturn(true);
        Mockito.when(jwtService.generateAccessToken(USER_ID, USERNAME, true))
                .thenReturn(ACCESS_TOKEN);
        Mockito.when(jwtService.generateRefreshToken(USER_ID, USERNAME))
                .thenReturn(REFRESH_TOKEN);

        // 2. Execute
        AuthResult result = authService.login(USERNAME, PASSWORD);

        // 3. Post-conditions
        assertNotNull(result);
        Mockito.verify(jwtService).generateAccessToken(USER_ID, USERNAME, true);
    }

    @Test(expected = UserNotFoundException.class)
    public void test_login_userNotFound() {
        // 1. Pre-conditions
        Mockito.when(userDao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        // 2. Execute
        authService.login(USERNAME, PASSWORD);

        // 3. Post-conditions - exception thrown
    }

    @Test
    public void test_login_invalidPassword() {
        // 1. Pre-conditions
        Mockito.when(userDao.findByUsername(USERNAME)).thenReturn(Optional.of(testUser));
        Mockito.when(passwordEncoder.matches(PASSWORD, HASHED_PASSWORD)).thenReturn(false);

        // 2. Execute
        try {
            authService.login(USERNAME, PASSWORD);
            fail("Expected RuntimeException for incorrect password");
        } catch (RuntimeException e) {
            // 3. Post-conditions
            assertEquals("exception.IncorrectPassword", e.getMessage());
            Mockito.verify(jwtService, Mockito.never()).generateAccessToken(anyLong(), anyString(), anyBoolean());
        }
    }

    @Test
    public void test_login_refreshTokenStored() {
        // 1. Pre-conditions
        Mockito.when(userDao.findByUsername(USERNAME)).thenReturn(Optional.of(testUser));
        Mockito.when(passwordEncoder.matches(PASSWORD, HASHED_PASSWORD)).thenReturn(true);
        Mockito.when(jwtService.generateAccessToken(anyLong(), anyString(), anyBoolean()))
                .thenReturn(ACCESS_TOKEN);
        Mockito.when(jwtService.generateRefreshToken(anyLong(), anyString()))
                .thenReturn(REFRESH_TOKEN);

        // 2. Execute
        authService.login(USERNAME, PASSWORD);

        // 3. Post-conditions - verify token was hashed before storing
        Mockito.verify(refreshTokenDao).save(refreshTokenCaptor.capture());
        RefreshToken savedToken = refreshTokenCaptor.getValue();

        assertNotNull(savedToken);
        assertNotEquals(REFRESH_TOKEN, savedToken.getToken()); // Should be hashed
        assertEquals(USER_ID, savedToken.getUserId());
    }

    // ========== REFRESH TESTS ==========

    @Test
    public void test_refresh_successfulTokenRefresh() {
        // 1. Pre-conditions
        Mockito.when(jwtService.validateRefreshToken(REFRESH_TOKEN)).thenReturn(true);
        Mockito.when(refreshTokenDao.findByToken(anyString()))
                .thenReturn(Optional.of(refreshTokenEntity));
        Mockito.when(jwtService.extractUserId(REFRESH_TOKEN)).thenReturn(USER_ID);
        Mockito.when(jwtService.extractUsername(REFRESH_TOKEN)).thenReturn(USERNAME);
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(testUser));
        Mockito.when(jwtService.generateAccessToken(USER_ID, USERNAME, false))
                .thenReturn(ACCESS_TOKEN);
        Mockito.when(jwtService.generateRefreshToken(USER_ID, USERNAME))
                .thenReturn("new_" + REFRESH_TOKEN);

        // 2. Execute
        AuthResult result = authService.refresh(REFRESH_TOKEN);

        // 3. Post-conditions
        assertNotNull(result);
        assertEquals(ACCESS_TOKEN, result.getAccessToken());
        assertEquals("new_" + REFRESH_TOKEN, result.getRefreshToken());
        assertEquals(testUser, result.getUser());

        Mockito.verify(refreshTokenDao).revokeToken(anyString()); // Old token revoked
        Mockito.verify(refreshTokenDao).save(any(RefreshToken.class)); // New token saved
    }

    @Test(expected = InvalidTokenException.class)
    public void test_refresh_invalidTokenFormat() {
        // 1. Pre-conditions
        Mockito.when(jwtService.validateRefreshToken(REFRESH_TOKEN)).thenReturn(false);

        // 2. Execute
        authService.refresh(REFRESH_TOKEN);

        // 3. Post-conditions - exception thrown
    }

    @Test(expected = InvalidTokenException.class)
    public void test_refresh_tokenNotInDatabase() {
        // 1. Pre-conditions
        Mockito.when(jwtService.validateRefreshToken(REFRESH_TOKEN)).thenReturn(true);
        Mockito.when(refreshTokenDao.findByToken(anyString())).thenReturn(Optional.empty());

        // 2. Execute
        authService.refresh(REFRESH_TOKEN);

        // 3. Post-conditions - exception thrown
    }

    @Test(expected = InvalidTokenException.class)
    public void test_refresh_expiredToken() {
        // 1. Pre-conditions
        RefreshToken expiredToken = new RefreshToken(HASHED_REFRESH_TOKEN, USER_ID,
                LocalDateTime.now().minusDays(1)); // Expired

        Mockito.when(jwtService.validateRefreshToken(REFRESH_TOKEN)).thenReturn(true);
        Mockito.when(refreshTokenDao.findByToken(anyString())).thenReturn(Optional.of(expiredToken));

        // 2. Execute
        authService.refresh(REFRESH_TOKEN);

        // 3. Post-conditions - exception thrown
    }

    @Test(expected = InvalidTokenException.class)
    public void test_refresh_userNotFound() {
        // 1. Pre-conditions
        Mockito.when(jwtService.validateRefreshToken(REFRESH_TOKEN)).thenReturn(true);
        Mockito.when(refreshTokenDao.findByToken(anyString()))
                .thenReturn(Optional.of(refreshTokenEntity));
        Mockito.when(jwtService.extractUserId(REFRESH_TOKEN)).thenReturn(USER_ID);
        Mockito.when(jwtService.extractUsername(REFRESH_TOKEN)).thenReturn(USERNAME);
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        // 2. Execute
        authService.refresh(REFRESH_TOKEN);

        // 3. Post-conditions - exception thrown
    }

    @Test
    public void test_refresh_tokenRotation() {
        // 1. Pre-conditions
        Mockito.when(jwtService.validateRefreshToken(REFRESH_TOKEN)).thenReturn(true);
        Mockito.when(refreshTokenDao.findByToken(anyString()))
                .thenReturn(Optional.of(refreshTokenEntity));
        Mockito.when(jwtService.extractUserId(REFRESH_TOKEN)).thenReturn(USER_ID);
        Mockito.when(jwtService.extractUsername(REFRESH_TOKEN)).thenReturn(USERNAME);
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(testUser));
        Mockito.when(jwtService.generateAccessToken(anyLong(), anyString(), anyBoolean()))
                .thenReturn(ACCESS_TOKEN);
        Mockito.when(jwtService.generateRefreshToken(anyLong(), anyString()))
                .thenReturn("new_" + REFRESH_TOKEN);

        // 2. Execute
        authService.refresh(REFRESH_TOKEN);

        // 3. Post-conditions - verify old token revoked, new token saved
        Mockito.verify(refreshTokenDao).revokeToken(anyString());
        Mockito.verify(refreshTokenDao).save(any(RefreshToken.class));
    }

    // ========== LOGOUT TESTS ==========

    @Test
    public void test_logout_successful() {
        // 1. Pre-conditions
        Mockito.when(refreshTokenDao.revokeToken(anyString())).thenReturn(true);

        // 2. Execute
        boolean result = authService.logout(REFRESH_TOKEN);

        // 3. Post-conditions
        assertTrue(result);
        Mockito.verify(refreshTokenDao).revokeToken(anyString());
    }

    @Test
    public void test_logout_tokenNotFound() {
        // 1. Pre-conditions
        Mockito.when(refreshTokenDao.revokeToken(anyString())).thenReturn(false);

        // 2. Execute
        boolean result = authService.logout(REFRESH_TOKEN);

        // 3. Post-conditions
        assertFalse(result);
    }

    @Test
    public void test_logout_handlesException() {
        // 1. Pre-conditions
        Mockito.when(refreshTokenDao.revokeToken(anyString()))
                .thenThrow(new RuntimeException("Database error"));

        // 2. Execute
        boolean result = authService.logout(REFRESH_TOKEN);

        // 3. Post-conditions
        assertFalse(result); // Should return false instead of throwing
    }

    // ========== VALIDATE REFRESH TOKEN TESTS ==========

    @Test
    public void test_validateRefreshToken_validToken() {
        // 1. Pre-conditions
        Mockito.when(jwtService.validateRefreshToken(REFRESH_TOKEN)).thenReturn(true);
        Mockito.when(refreshTokenDao.isValidToken(anyString())).thenReturn(true);

        // 2. Execute
        boolean result = authService.validateRefreshToken(REFRESH_TOKEN);

        // 3. Post-conditions
        assertTrue(result);
    }

    @Test
    public void test_validateRefreshToken_invalidJwtFormat() {
        // 1. Pre-conditions
        Mockito.when(jwtService.validateRefreshToken(REFRESH_TOKEN)).thenReturn(false);

        // 2. Execute
        boolean result = authService.validateRefreshToken(REFRESH_TOKEN);

        // 3. Post-conditions
        assertFalse(result);
        Mockito.verify(refreshTokenDao, Mockito.never()).isValidToken(anyString());
    }

    @Test
    public void test_validateRefreshToken_notInDatabase() {
        // 1. Pre-conditions
        Mockito.when(jwtService.validateRefreshToken(REFRESH_TOKEN)).thenReturn(true);
        Mockito.when(refreshTokenDao.isValidToken(anyString())).thenReturn(false);

        // 2. Execute
        boolean result = authService.validateRefreshToken(REFRESH_TOKEN);

        // 3. Post-conditions
        assertFalse(result);
    }

    @Test
    public void test_validateRefreshToken_handlesException() {
        // 1. Pre-conditions
        Mockito.when(jwtService.validateRefreshToken(REFRESH_TOKEN))
                .thenThrow(new RuntimeException("Token error"));

        // 2. Execute
        boolean result = authService.validateRefreshToken(REFRESH_TOKEN);

        // 3. Post-conditions
        assertFalse(result); // Should return false instead of throwing
    }

    // ========== GET CURRENT USER TESTS ==========

    @Test
    public void test_getCurrentUser_validToken() {
        // 1. Pre-conditions
        Mockito.when(jwtService.validateAccessToken(ACCESS_TOKEN)).thenReturn(true);
        Mockito.when(jwtService.extractUserId(ACCESS_TOKEN)).thenReturn(USER_ID);
        Mockito.when(userService.findUserById(USER_ID)).thenReturn(testUser);

        // 2. Execute
        User result = authService.getCurrentUser(ACCESS_TOKEN);

        // 3. Post-conditions
        assertNotNull(result);
        assertEquals(testUser, result);
        Mockito.verify(userService).findUserById(USER_ID);
    }

    @Test(expected = InvalidTokenException.class)
    public void test_getCurrentUser_invalidToken() {
        // 1. Pre-conditions
        Mockito.when(jwtService.validateAccessToken(ACCESS_TOKEN)).thenReturn(false);

        // 2. Execute
        authService.getCurrentUser(ACCESS_TOKEN);

        // 3. Post-conditions - exception thrown
    }

    @Test(expected = InvalidTokenException.class)
    public void test_getCurrentUser_extractionError() {
        // 1. Pre-conditions
        Mockito.when(jwtService.validateAccessToken(ACCESS_TOKEN)).thenReturn(true);
        Mockito.when(jwtService.extractUserId(ACCESS_TOKEN))
                .thenThrow(new RuntimeException("Token parse error"));

        // 2. Execute
        authService.getCurrentUser(ACCESS_TOKEN);

        // 3. Post-conditions - exception thrown
    }

    // ========== INTEGRATION TESTS ==========

    @Test
    public void test_loginAndRefresh_fullFlow() {
        // 1. Pre-conditions - Login
        Mockito.when(userDao.findByUsername(USERNAME)).thenReturn(Optional.of(testUser));
        Mockito.when(passwordEncoder.matches(PASSWORD, HASHED_PASSWORD)).thenReturn(true);
        Mockito.when(jwtService.generateAccessToken(anyLong(), anyString(), anyBoolean()))
                .thenReturn(ACCESS_TOKEN);
        Mockito.when(jwtService.generateRefreshToken(anyLong(), anyString()))
                .thenReturn(REFRESH_TOKEN);

        // 2. Execute - Login
        AuthResult loginResult = authService.login(USERNAME, PASSWORD);

        // 3. Post-conditions - Login
        assertNotNull(loginResult);
        assertEquals(ACCESS_TOKEN, loginResult.getAccessToken());

        // 4. Pre-conditions - Refresh
        Mockito.when(jwtService.validateRefreshToken(REFRESH_TOKEN)).thenReturn(true);
        Mockito.when(refreshTokenDao.findByToken(anyString()))
                .thenReturn(Optional.of(refreshTokenEntity));
        Mockito.when(jwtService.extractUserId(REFRESH_TOKEN)).thenReturn(USER_ID);
        Mockito.when(jwtService.extractUsername(REFRESH_TOKEN)).thenReturn(USERNAME);
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(testUser));
        Mockito.when(jwtService.generateRefreshToken(anyLong(), anyString()))
                .thenReturn("new_" + REFRESH_TOKEN);

        // 5. Execute - Refresh
        AuthResult refreshResult = authService.refresh(REFRESH_TOKEN);

        // 6. Post-conditions - Refresh
        assertNotNull(refreshResult);
        assertNotEquals(loginResult.getRefreshToken(), refreshResult.getRefreshToken());
        Mockito.verify(refreshTokenDao).revokeToken(anyString()); // Old token revoked
    }
}
