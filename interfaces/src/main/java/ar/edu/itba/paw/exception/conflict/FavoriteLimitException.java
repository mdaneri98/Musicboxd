package ar.edu.itba.paw.exception.conflict;

public class FavoriteLimitException extends ConflictException {
    public FavoriteLimitException(Long userId) {
        super("User with ID: " + userId + " has reached the maximum number of favorites");
    }

    public FavoriteLimitException(Long userId, Throwable cause) {
        super("User with ID: " + userId + " has reached the maximum number of favorites", cause);
    }
}