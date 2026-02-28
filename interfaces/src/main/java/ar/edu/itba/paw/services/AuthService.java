package ar.edu.itba.paw.services;

import ar.edu.itba.paw.domain.user.User;
import ar.edu.itba.paw.models.AuthResult;

public interface AuthService {

    /**
     * Authenticate user and generate tokens
     * @param username the username
     * @param password the password
     * @return AuthResult with access token, refresh token and user info
     */
    AuthResult login(String username, String password);

    /**
     * Refresh access token using refresh token
     * @param refreshToken the refresh token
     * @return new AuthResult with access token and refresh token
     */
    AuthResult refresh(String refreshToken);

    /**
     * Logout user by revoking refresh token
     * @param refreshToken the refresh token to revoke
     * @return true if logout successful
     */
    boolean logout(String refreshToken);

    /**
     * Validate refresh token
     * @param refreshToken the token to validate
     * @return true if valid
     */
    boolean validateRefreshToken(String refreshToken);

    /**
     * Get current user info from access token
     * @param accessToken the access token
     * @return user model
     */
    User getCurrentUser(String accessToken);
}
