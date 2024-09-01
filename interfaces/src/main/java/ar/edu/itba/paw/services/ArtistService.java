package ar.edu.itba.paw.services;

import ar.edu.itba.paw.Artist;

import java.util.List;
import java.util.Optional;

public interface ArtistService {
    Optional<Artist> findById(long id);

    List<Artist> findAll();

    List<Artist> findBySongId(long id);

    int save(Artist artist);

    int update(Artist artist);

    int deleteById(long id);

}
