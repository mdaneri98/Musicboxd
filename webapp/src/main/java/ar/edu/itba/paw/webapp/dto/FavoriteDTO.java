package ar.edu.itba.paw.webapp.dto;

import java.time.LocalDateTime;

public class FavoriteDTO {
    private Long id; // The ID of the item
    private Long userId;
    private Long itemId;
    private String type;
    private LocalDateTime createdAt;

    public FavoriteDTO() {
    }

    public FavoriteDTO(Long userId, Long itemId, String type, LocalDateTime createdAt) {
        this.id = itemId;
        this.userId = userId;
        this.itemId = itemId;
        this.type = type;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
