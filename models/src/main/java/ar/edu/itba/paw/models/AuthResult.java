package ar.edu.itba.paw.models;

/**
 * Result object for authentication operations.
 * Contains the authenticated user and generated tokens.
 */
public class AuthResult {
    
    private String accessToken;
    private String refreshToken;
    private User user;

    public AuthResult() {}

    public AuthResult(String accessToken, String refreshToken, User user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

