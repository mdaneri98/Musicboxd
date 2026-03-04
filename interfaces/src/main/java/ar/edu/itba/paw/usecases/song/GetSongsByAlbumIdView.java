package ar.edu.itba.paw.usecases.song;

import ar.edu.itba.paw.views.SongView;
import java.util.List;

public interface GetSongsByAlbumIdView {
    List<SongView> execute(Long albumId);
}
