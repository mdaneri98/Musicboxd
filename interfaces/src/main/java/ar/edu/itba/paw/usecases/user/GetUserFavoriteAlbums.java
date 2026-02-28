package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.models.Album;
import java.util.List;

public interface GetUserFavoriteAlbums {
    List<Album> execute(Long userId, Integer page, Integer size);
}
