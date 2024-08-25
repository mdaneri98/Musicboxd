package ar.edu.itba.paw.services;

import ar.edu.itba.paw.Album;

import java.util.List;
import java.util.Optional;

public interface AlbumService {

    Optional<Album> findById(long id);
    List<Album> findAll();

    int save(Album album);

    int update(Album album);

    int deleteById(long id);

}
