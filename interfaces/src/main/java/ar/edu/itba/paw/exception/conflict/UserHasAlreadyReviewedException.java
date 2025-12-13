package ar.edu.itba.paw.exception.conflict;

public class UserHasAlreadyReviewedException extends ConflictException {
    public UserHasAlreadyReviewedException(Long userId, Long itemId, String itemType) {
        super("exception.UserAlreadyReviewed");
    }

    public UserHasAlreadyReviewedException(Long userId, Long itemId, String itemType, Throwable cause) {
        super("exception.UserAlreadyReviewed", cause);
    }
}
