package ar.edu.itba.paw.usecases.album;

import ar.edu.itba.paw.domain.album.Album;

public interface GetAlbum {
    Album execute(Long albumId);
}
