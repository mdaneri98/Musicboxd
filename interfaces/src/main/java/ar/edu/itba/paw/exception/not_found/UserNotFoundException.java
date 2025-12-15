package ar.edu.itba.paw.exception.not_found;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(Long id) {
        super("exception.UserNotFound");
    }

    public UserNotFoundException(String username) {
        super("exception.UserNotFound");
    }

    public UserNotFoundException(Long id, Throwable cause) {
        super("exception.UserNotFound", cause);
    }

    public UserNotFoundException(String content, String type) {
        super("exception.UserNotFound");
    }

    public UserNotFoundException(String content, String type, Throwable cause) {
        super("exception.UserNotFound", cause);
    }
}