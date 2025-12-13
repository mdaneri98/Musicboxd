package ar.edu.itba.paw.exception.not_found;

public class ReviewNotFoundException extends EntityNotFoundException {
    public ReviewNotFoundException(Long id) {
        super("exception.ReviewNotFound");
    }

    public ReviewNotFoundException(Long id, Throwable cause) {
        super("exception.ReviewNotFound", cause);
    }

    public ReviewNotFoundException(Long userId, Long itemId, String itemType) {
        super("exception.ReviewNotFound");
    }

    public ReviewNotFoundException(Long userId, Long itemId, String itemType, Throwable cause) {
        super("exception.ReviewNotFound", cause);
    }
}