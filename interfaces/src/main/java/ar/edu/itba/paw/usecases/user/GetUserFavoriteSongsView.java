package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.views.SongView;

import java.util.List;

public interface GetUserFavoriteSongsView {
    List<SongView> execute(Long userId, Integer page, Integer size);
}
