package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Song;
import java.util.List;
import java.util.Optional;

public interface SongService {
    Optional<Song> findById(long id);
    List<Song> findAll();
    List<Song> findByArtistId(long id);
    List<Song> findByAlbumId(long id);
    List<Song> findByTitleContaining(String sub);
    long save(Song song);
    int update(Song song);
    int update(Song song, Song updatedSong);
    int deleteById(long id);
}
