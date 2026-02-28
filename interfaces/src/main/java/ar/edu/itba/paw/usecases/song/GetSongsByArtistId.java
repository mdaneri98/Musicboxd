package ar.edu.itba.paw.usecases.song;

import ar.edu.itba.paw.domain.song.Song;
import ar.edu.itba.paw.models.FilterType;

import java.util.List;

public interface GetSongsByArtistId {
    List<Song> execute(Long artistId, FilterType filterType, int page, int size);
}
