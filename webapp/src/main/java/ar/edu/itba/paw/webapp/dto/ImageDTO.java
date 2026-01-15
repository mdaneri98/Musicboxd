package ar.edu.itba.paw.webapp.dto;

import java.net.URI;

/**
 * DTO for Image resource
 */
public class ImageDTO {
    private Long id;
    private URI self;

    public ImageDTO() {
    }

    public ImageDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}
