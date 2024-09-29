package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;

import java.util.List;
import java.util.Optional;

public interface ArtistService extends CrudService<Artist> {

    List<Artist> findBySongId(long id);

    List<Artist> findByNameContaining(String sub);

}
