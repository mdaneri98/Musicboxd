package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.RefreshToken;
import java.util.Optional;

public interface RefreshTokenDao {
    
    /**
     * Save a refresh token to the database
     * @param refreshToken the token to save
     * @return the saved token
     */
    RefreshToken save(RefreshToken refreshToken);
    
    /**
     * Find a refresh token by its token value
     * @param token the token value to search for
     * @return Optional containing the token if found
     */
    Optional<RefreshToken> findByToken(String token);
    
    /**
     * Revoke a specific refresh token
     * @param token the token to revoke
     * @return true if token was found and revoked
     */
    boolean revokeToken(String token);
    
    /**
     * Revoke all refresh tokens for a specific user
     * @param userId the user ID
     * @return number of tokens revoked
     */
    int revokeAllUserTokens(Long userId);
    
    /**
     * Delete all expired refresh tokens
     * @return number of tokens deleted
     */
    int deleteExpired();
    
    /**
     * Check if a token exists and is valid
     * @param token the token to check
     * @return true if token exists and is not revoked and not expired
     */
    boolean isValidToken(String token);
}
