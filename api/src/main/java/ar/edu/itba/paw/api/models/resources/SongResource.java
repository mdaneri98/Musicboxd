package ar.edu.itba.paw.api.models.resources;

import ar.edu.itba.paw.models.dtos.SongDTO;

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

