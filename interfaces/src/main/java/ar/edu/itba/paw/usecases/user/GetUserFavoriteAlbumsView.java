package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.views.AlbumView;

import java.util.List;

public interface GetUserFavoriteAlbumsView {
    List<AlbumView> execute(Long userId, Integer page, Integer size);
}
