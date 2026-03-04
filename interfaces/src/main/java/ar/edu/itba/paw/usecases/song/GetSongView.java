package ar.edu.itba.paw.usecases.song;

import ar.edu.itba.paw.views.SongView;

public interface GetSongView {
    SongView execute(Long id);
}
