package ar.edu.itba.paw.usecases.song;

import ar.edu.itba.paw.domain.song.Song;

import java.util.List;

public interface SearchSongsByTitle {
    List<Song> execute(String titleSubstring, int page, int size);
}
