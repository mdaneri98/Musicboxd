package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;

import java.util.List;
import java.util.Optional;

public interface AlbumService extends CrudService<Album> {

    List<Album> findByArtistId(long id);

    List<Album> findByTitleContaining(String sub);

}
