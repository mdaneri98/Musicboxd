package ar.edu.itba.paw.usecases.user;

public interface RemoveFavoriteAlbum {
    void execute(Long userId, Long albumId);
}
