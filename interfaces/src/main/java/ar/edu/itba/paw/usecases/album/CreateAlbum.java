package ar.edu.itba.paw.usecases.album;

import ar.edu.itba.paw.domain.album.Album;

public interface CreateAlbum {
    Album execute(CreateAlbumCommand command);
}
