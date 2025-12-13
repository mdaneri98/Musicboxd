package ar.edu.itba.paw.exception.conflict;

public class FavoriteLimitException extends ConflictException {
    public FavoriteLimitException(Long userId) {
        super("exception.FavoriteLimit");
    }

    public FavoriteLimitException(Long userId, Throwable cause) {
        super("exception.FavoriteLimit", cause);
    }
}