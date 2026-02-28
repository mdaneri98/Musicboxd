package ar.edu.itba.paw.usecases.user;

public interface AddFavoriteSong {
    void execute(Long userId, Long songId);
}
