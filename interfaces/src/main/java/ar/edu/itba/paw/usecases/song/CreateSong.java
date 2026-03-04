package ar.edu.itba.paw.usecases.song;

import ar.edu.itba.paw.domain.song.Song;

public interface CreateSong {
    Song execute(CreateSongCommand command);
}
