package ar.edu.itba.paw.usecases.album;

import ar.edu.itba.paw.views.AlbumView;
import java.util.List;

public interface SearchAlbumsByTitleView {
    List<AlbumView> execute(String title, Integer page, Integer size);
}
