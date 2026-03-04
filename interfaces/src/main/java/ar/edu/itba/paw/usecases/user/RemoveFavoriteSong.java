package ar.edu.itba.paw.usecases.user;

public interface RemoveFavoriteSong {
    void execute(Long userId, Long songId);
}
