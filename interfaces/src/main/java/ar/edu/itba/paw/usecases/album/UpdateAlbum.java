package ar.edu.itba.paw.usecases.album;

import ar.edu.itba.paw.domain.album.Album;

public interface UpdateAlbum {
    Album execute(UpdateAlbumCommand command);
}
