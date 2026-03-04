package ar.edu.itba.paw.usecases.album;

import ar.edu.itba.paw.domain.album.Album;

import java.util.List;

public interface GetAlbumsByArtistId {
    List<Album> execute(Long artistId);
}
