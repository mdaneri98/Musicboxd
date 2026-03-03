package ar.edu.itba.paw.usecases.album;

import ar.edu.itba.paw.views.AlbumView;
import java.util.List;

public interface GetAllAlbumsView {
    List<AlbumView> execute(Integer page, Integer size);
}
