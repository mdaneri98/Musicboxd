package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.song.Song;
import java.util.List;

public interface GetUserFavoriteSongs {
    List<Song> execute(Long userId, Integer page, Integer size);
}
