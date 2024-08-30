package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.Song;

import java.util.List;
import java.util.Optional;

public interface SongDao {

    Optional<Song> findById(long id);
    List<Song> findAll();
    List<Song> findByArtistId(long id);
    int save(Song song);
    int update(Song song);
    int deleteById(long id);
}

