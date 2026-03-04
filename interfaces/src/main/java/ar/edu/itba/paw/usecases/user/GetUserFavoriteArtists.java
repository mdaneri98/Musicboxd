package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.artist.Artist;
import java.util.List;

public interface GetUserFavoriteArtists {
    List<Artist> execute(Long userId, Integer page, Integer size);
}
