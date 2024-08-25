package ar.edu.itba.paw.services;

import ar.edu.itba.paw.Song;
import java.util.List;
import java.util.Optional;

public interface SongService {
    Optional<Song> findById(long id);
    List<Song> findAll();
    int save(Song song);
    int update(Song song);
    int deleteById(long id);
}
