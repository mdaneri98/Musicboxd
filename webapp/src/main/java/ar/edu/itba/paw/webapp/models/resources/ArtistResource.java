package ar.edu.itba.paw.webapp.models.resources;

import ar.edu.itba.paw.webapp.dto.ArtistDTO;

/**
 * HATEOAS resource wrapper for Artist entities
 */
public class ArtistResource extends Resource<ArtistDTO> {

    private final ArtistDTO item;

    public ArtistResource(ArtistDTO item) {
        this.item = item;
    }

    @Override
    public ArtistDTO getData() {
        return item;
    }
}
