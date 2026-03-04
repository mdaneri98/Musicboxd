package ar.edu.itba.paw.usecases.album;

import ar.edu.itba.paw.domain.album.Album;

import java.util.List;

public interface GetAllAlbums {
    List<Album> execute(Integer page, Integer size);
    Long count();
}
