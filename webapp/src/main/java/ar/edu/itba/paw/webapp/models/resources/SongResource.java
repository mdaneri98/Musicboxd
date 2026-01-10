package ar.edu.itba.paw.webapp.models.resources;

import ar.edu.itba.paw.webapp.dto.SongDTO;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * HATEOAS resource wrapper for Song entities
 */
public class SongResource extends Resource<SongDTO> {

    @JsonUnwrapped
    private final SongDTO item;

    public SongResource(SongDTO item) {
        this.item = item;
    }
}
