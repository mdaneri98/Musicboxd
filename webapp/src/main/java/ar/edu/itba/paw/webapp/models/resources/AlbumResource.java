package ar.edu.itba.paw.webapp.models.resources;

import ar.edu.itba.paw.webapp.dto.AlbumDTO;

/**
 * HATEOAS resource wrapper for Album entities
 */
public class AlbumResource extends Resource<AlbumDTO> {

    private final AlbumDTO item;

    public AlbumResource(AlbumDTO item) {
        this.item = item;
    }

    @Override
    public AlbumDTO getData() {
        return item;
    }
}
