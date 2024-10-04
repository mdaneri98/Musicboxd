package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.dtos.SongDTO;

import java.util.List;
import java.util.Optional;

public interface SongService extends CrudService<Song> {

    List<Song> findByArtistId(long id);
    List<Song> findByAlbumId(long id);
    List<Song> findByTitleContaining(String sub);

    Song save(SongDTO songDTO, Album album);
    boolean save(List<SongDTO> songsDTO, Album album);
    Song update(SongDTO songDTO, Album album);
    boolean update(List<SongDTO> songsDTO, Album album);
}
