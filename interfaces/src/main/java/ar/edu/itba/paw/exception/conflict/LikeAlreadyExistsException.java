package ar.edu.itba.paw.exception.conflict;

public class LikeAlreadyExistsException extends ConflictException {
    public LikeAlreadyExistsException(Long userId, Long reviewId) {
        super("User with id: " + userId + " already liked review with id: " + reviewId);
    }

    public LikeAlreadyExistsException(Long userId, Long reviewId, Throwable cause) {
        super("User with id: " + userId + " already liked review with id: " + reviewId, cause);
    }
}
