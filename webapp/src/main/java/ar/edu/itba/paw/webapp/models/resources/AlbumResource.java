package ar.edu.itba.paw.webapp.models.resources;

import ar.edu.itba.paw.webapp.dto.AlbumDTO;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * HATEOAS resource wrapper for Album entities
 */
public class AlbumResource extends Resource<AlbumDTO> {

    @JsonUnwrapped
    private final AlbumDTO item;

    public AlbumResource(AlbumDTO item) {
        this.item = item;
    }
}
