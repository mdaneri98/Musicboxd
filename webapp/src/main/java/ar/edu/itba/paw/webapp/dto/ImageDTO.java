package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.webapp.dto.links.ImageLinksDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for Image resource
 */
public class ImageDTO {
    private Long id;

    @JsonProperty("_links")
    private ImageLinksDTO links;

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

    public ImageLinksDTO getLinks() {
        return links;
    }

    public void setLinks(ImageLinksDTO links) {
        this.links = links;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
