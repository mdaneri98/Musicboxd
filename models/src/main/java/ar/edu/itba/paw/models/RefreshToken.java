package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refresh_token_id_seq")
    @SequenceGenerator(sequenceName = "refresh_token_id_seq", name = "refresh_token_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "token", length = 255, unique = true, nullable = false)
    private String token;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_revoked", nullable = false)
    private Boolean isRevoked;

    public RefreshToken() {}

    public RefreshToken(String token, Long userId, LocalDateTime expiresAt) {
        this.token = token;
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.createdAt = LocalDateTime.now();
        this.isRevoked = false;
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

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !isRevoked && !isExpired();
    }
}
