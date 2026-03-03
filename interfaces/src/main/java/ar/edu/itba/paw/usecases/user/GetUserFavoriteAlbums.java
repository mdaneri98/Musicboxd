package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.album.Album;
import java.util.List;

public interface GetUserFavoriteAlbums {
    List<Album> execute(Long userId, Integer page, Integer size);
}
