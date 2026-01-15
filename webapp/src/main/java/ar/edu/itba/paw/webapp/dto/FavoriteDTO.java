package ar.edu.itba.paw.webapp.dto;

import java.net.URI;
import java.time.LocalDateTime;

public class FavoriteDTO {
    private Long id; // The ID of the item
    private Long userId;
    private Long itemId;
    private String type;
    private LocalDateTime createdAt;

    // HATEOAS links
    private URI self;
    private URI userLink;
    private URI itemLink;

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

    // HATEOAS getters and setters
    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getUserLink() {
        return userLink;
    }

    public void setUserLink(URI userLink) {
        this.userLink = userLink;
    }

    public URI getItemLink() {
        return itemLink;
    }

    public void setItemLink(URI itemLink) {
        this.itemLink = itemLink;
    }
}
