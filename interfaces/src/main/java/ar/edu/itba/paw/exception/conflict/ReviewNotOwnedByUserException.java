package ar.edu.itba.paw.exception.conflict;

public class ReviewNotOwnedByUserException extends ConflictException {
    public ReviewNotOwnedByUserException(Long id, Long userId) {
        super("exception.ReviewNotOwnedByUser");
    }

    public ReviewNotOwnedByUserException(Long id, Long userId, Throwable cause) {
        super("exception.ReviewNotOwnedByUser", cause);
    }
}
