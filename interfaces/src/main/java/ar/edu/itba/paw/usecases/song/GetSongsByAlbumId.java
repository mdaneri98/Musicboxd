package ar.edu.itba.paw.usecases.song;

import ar.edu.itba.paw.domain.song.Song;

import java.util.List;

public interface GetSongsByAlbumId {
    List<Song> execute(Long albumId);
}
