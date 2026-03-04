package ar.edu.itba.paw.usecases.song;

import ar.edu.itba.paw.domain.song.Song;

import java.util.List;

public interface GetAllSongs {
    List<Song> execute(int page, int size);
    Long count();
}
