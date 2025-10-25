package ar.edu.itba.paw.services.exception.conflict;

public class UserHasAlreadyReviewedException extends ConflictException {
    public UserHasAlreadyReviewedException(Long userId, Long itemId, String itemType) {
        super("User with id " + userId + " has already reviewed " + itemType + " with id " + itemId);
    }

    public UserHasAlreadyReviewedException(Long userId, Long itemId, String itemType, Throwable cause) {
        super("User with id " + userId + " has already reviewed " + itemType + " with id " + itemId, cause);
    }
}
