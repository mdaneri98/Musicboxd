package ar.edu.itba.paw.usecases.song;

import ar.edu.itba.paw.views.SongView;
import java.util.List;

public interface SearchSongsByTitleView {
    List<SongView> execute(String title, Integer page, Integer size);
}
