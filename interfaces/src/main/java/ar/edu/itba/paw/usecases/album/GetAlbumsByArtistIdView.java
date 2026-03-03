package ar.edu.itba.paw.usecases.album;

import ar.edu.itba.paw.views.AlbumView;
import java.util.List;

public interface GetAlbumsByArtistIdView {
    List<AlbumView> execute(Long artistId);
}
