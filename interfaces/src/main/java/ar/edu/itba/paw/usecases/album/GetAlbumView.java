package ar.edu.itba.paw.usecases.album;

import ar.edu.itba.paw.views.AlbumView;

public interface GetAlbumView {
    AlbumView execute(Long id);
}
