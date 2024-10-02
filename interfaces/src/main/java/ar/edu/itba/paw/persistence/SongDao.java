package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;

import java.util.List;
import java.util.Optional;

public interface SongDao {

    Optional<Song> findById(long id);
    List<Song> findAll();
    List<Song> findByArtistId(long id);
    List<Song> findByAlbumId(long id);
    List<Song> findByTitleContaining(String sub);

    long save(Song song);
    int saveSongArtist(Song song, Artist artist);
    int update(Song song);
    int deleteById(long id);
}

