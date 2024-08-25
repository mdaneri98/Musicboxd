package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.Album;
import ar.edu.itba.paw.User;

import java.util.List;
import java.util.Optional;

public interface AlbumDao {

    Optional<Album> findById(long id);
    List<Album> findAll();

    int save(Album album);

    int update(Album album);

    int deleteById(long id);

}
