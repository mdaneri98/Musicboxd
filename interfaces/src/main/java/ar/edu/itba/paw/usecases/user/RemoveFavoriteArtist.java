package ar.edu.itba.paw.usecases.user;

public interface RemoveFavoriteArtist {
    void execute(Long userId, Long artistId);
}
