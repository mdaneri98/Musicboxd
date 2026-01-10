package ar.edu.itba.paw.webapp.models.resources;

import ar.edu.itba.paw.webapp.dto.ArtistDTO;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * HATEOAS resource wrapper for Artist entities
 */
public class ArtistResource extends Resource<ArtistDTO> {

    @JsonUnwrapped
    private final ArtistDTO item;

    public ArtistResource(ArtistDTO item) {
        this.item = item;
    }
}
