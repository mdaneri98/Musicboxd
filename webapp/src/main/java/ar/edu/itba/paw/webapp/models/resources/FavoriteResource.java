package ar.edu.itba.paw.webapp.models.resources;

import ar.edu.itba.paw.webapp.dto.FavoriteDTO;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * HATEOAS resource wrapper for Favorite entities
 */
public class FavoriteResource extends Resource<FavoriteDTO> {

    @JsonUnwrapped
    private final FavoriteDTO item;

    public FavoriteResource(FavoriteDTO item) {
        this.item = item;
    }
}
