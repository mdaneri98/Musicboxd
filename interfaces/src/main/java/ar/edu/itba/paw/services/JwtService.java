package ar.edu.itba.paw.services;

public interface JwtService {
    
    /**
     * Generate an access token for a user
     * @param userId the user ID
     * @param username the username
     * @param isModerator whether the user is a moderator
     * @return the access token
     */
    String generateAccessToken(Long userId, String username, boolean isModerator);
    
    /**
     * Generate a refresh token for a user
     * @param userId the user ID
     * @param username the username
     * @return the refresh token
     */
    String generateRefreshToken(Long userId, String username);
    
    /**
     * Validate an access token
     * @param token the token to validate
     * @return true if valid
     */
    boolean validateAccessToken(String token);
    
    /**
     * Validate a refresh token
     * @param token the token to validate
     * @return true if valid
     */
    boolean validateRefreshToken(String token);
    
    /**
     * Extract user ID from token
     * @param token the JWT token
     * @return the user ID
     */
    Long extractUserId(String token);
    
    /**
     * Extract username from token
     * @param token the JWT token
     * @return the username
     */
    String extractUsername(String token);
    
    /**
     * Extract roles from token
     * @param token the JWT token
     * @return the roles string
     */
    String extractRoles(String token);
    
    /**
     * Check if token is expired
     * @param token the JWT token
     * @return true if expired
     */
    boolean isTokenExpired(String token);
}
