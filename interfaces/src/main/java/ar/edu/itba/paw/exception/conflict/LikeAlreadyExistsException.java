package ar.edu.itba.paw.exception.conflict;

public class LikeAlreadyExistsException extends ConflictException {
    public LikeAlreadyExistsException(Long userId, Long reviewId) {
        super("exception.LikeAlreadyExists");
    }

    public LikeAlreadyExistsException(Long userId, Long reviewId, Throwable cause) {
        super("exception.LikeAlreadyExists", cause);
    }
}
