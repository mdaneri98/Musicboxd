package ar.edu.itba.paw.exception.conflict;

public class UserAlreadyReviewedException extends ConflictException {
    public UserAlreadyReviewedException(Long userId, Long itemId, String itemType) {
        super("exception.UserAlreadyReviewed");
    }

    public UserAlreadyReviewedException(Long userId, Long itemId, String itemType, Throwable cause) {
        super("exception.UserAlreadyReviewed", cause);
    }
}
