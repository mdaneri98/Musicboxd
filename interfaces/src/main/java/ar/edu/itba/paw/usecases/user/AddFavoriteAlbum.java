package ar.edu.itba.paw.usecases.user;

public interface AddFavoriteAlbum {
    void execute(Long userId, Long albumId);
}
