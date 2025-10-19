package ar.edu.itba.paw.models.dtos;

import java.time.LocalDateTime;

public class RefreshTokenDTO {
    
    private Long id;
    private String token;
    private Long userId;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private Boolean isRevoked;

    public RefreshTokenDTO() {}

    public RefreshTokenDTO(Long id, String token, Long userId, LocalDateTime expiresAt, 
                          LocalDateTime createdAt, Boolean isRevoked) {
        this.id = id;
        this.token = token;
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.isRevoked = isRevoked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsRevoked() {
        return isRevoked;
    }

    public void setIsRevoked(Boolean isRevoked) {
        this.isRevoked = isRevoked;
    }
}
