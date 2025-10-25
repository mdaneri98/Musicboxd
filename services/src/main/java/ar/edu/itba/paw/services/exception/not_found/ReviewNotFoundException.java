package ar.edu.itba.paw.services.exception.not_found;

public class ReviewNotFoundException extends EntityNotFoundException {
    public ReviewNotFoundException(Long id) {
        super("Review with id " + id + " not found");
    }

    public ReviewNotFoundException(Long id, Throwable cause) {
        super("Review with id " + id + " not found", cause);
    }

    public ReviewNotFoundException(Long userId, Long itemId, String itemType) {
        super("Review with user id " + userId + " and " + itemType + " id " + itemId + " not found");
    }

    public ReviewNotFoundException(Long userId, Long itemId, String itemType, Throwable cause) {
        super("Review with user id " + userId + " and " + itemType + " id " + itemId + " not found", cause);
    }
}