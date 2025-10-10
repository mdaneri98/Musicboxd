package ar.edu.itba.paw.api.models;

import ar.edu.itba.paw.models.dtos.ArtistDTO;

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

