package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Album;

import java.util.List;
import java.util.Optional;

public interface AlbumDao {

    Optional<Album> findById(long id);
    List<Album> findByArtistId(long id);
    List<Album> findAll();
    List<Album> findByTitleContaining(String sub);

    long save(Album album);

    int update(Album album);

    int deleteById(long id);

}
