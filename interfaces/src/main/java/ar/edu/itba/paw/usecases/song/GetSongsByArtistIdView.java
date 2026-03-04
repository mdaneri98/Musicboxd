package ar.edu.itba.paw.usecases.song;

import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.views.SongView;
import java.util.List;

public interface GetSongsByArtistIdView {
    List<SongView> execute(Long artistId, FilterType filter, Integer page, Integer size);
}
