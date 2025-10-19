package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.dtos.LoginRequestDTO;
import ar.edu.itba.paw.models.dtos.LoginResponseDTO;
import ar.edu.itba.paw.models.dtos.UserDTO;

public interface AuthService {
    
    /**
     * Authenticate user and generate tokens
     * @param loginRequest the login credentials
     * @return login response with access token, refresh token and user info
     */
    LoginResponseDTO login(LoginRequestDTO loginRequest);
    
    /**
     * Refresh access token using refresh token
     * @param refreshToken the refresh token
     * @return new access token and refresh token
     */
    LoginResponseDTO refresh(String refreshToken);
    
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
     * @return user info
     */
    UserDTO getCurrentUser(String accessToken);
}
