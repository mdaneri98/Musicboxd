package ar.edu.itba.paw.usecases.song;

import ar.edu.itba.paw.views.SongView;
import java.util.List;

public interface GetAllSongsView {
    List<SongView> execute(Integer page, Integer size);
}
