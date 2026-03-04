package ar.edu.itba.paw.usecases.album;

import ar.edu.itba.paw.domain.album.Album;

import java.util.List;

public interface SearchAlbumsByTitle {
    List<Album> execute(String titleSubstring, Integer page, Integer size);
}
