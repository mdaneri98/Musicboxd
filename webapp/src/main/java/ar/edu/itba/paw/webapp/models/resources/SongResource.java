package ar.edu.itba.paw.webapp.models.resources;

import ar.edu.itba.paw.webapp.dto.SongDTO;

/**
 * HATEOAS resource wrapper for Song entities
 */
public class SongResource extends Resource<SongDTO> {

    private final SongDTO item;

    public SongResource(SongDTO item) {
        this.item = item;
    }

    @Override
    public SongDTO getData() {
        return item;
    }
}
