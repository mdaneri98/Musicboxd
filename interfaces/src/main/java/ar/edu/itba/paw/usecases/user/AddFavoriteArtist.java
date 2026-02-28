package ar.edu.itba.paw.usecases.user;

public interface AddFavoriteArtist {
    void execute(Long userId, Long artistId);
}
