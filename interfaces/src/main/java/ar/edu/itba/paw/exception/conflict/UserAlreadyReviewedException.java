package ar.edu.itba.paw.exception.conflict;

public class UserAlreadyReviewedException extends ConflictException {
    public UserAlreadyReviewedException(Long userId, Long itemId, String itemType) {
        super("User with id " + userId + " has already reviewed " + itemType + " with id " + itemId);
    }

    public UserAlreadyReviewedException(Long userId, Long itemId, String itemType, Throwable cause) {
        super("User with id " + userId + " has already reviewed " + itemType + " with id " + itemId, cause);
    }
}
