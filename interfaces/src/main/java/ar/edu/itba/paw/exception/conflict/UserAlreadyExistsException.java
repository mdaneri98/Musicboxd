package ar.edu.itba.paw.exception.conflict;

public class UserAlreadyExistsException extends ConflictException {
    public UserAlreadyExistsException(String content, String type) {
        super("exception.UserAlreadyExists");
    }

    public UserAlreadyExistsException(String content, String type, Throwable cause) {
        super("exception.UserAlreadyExists", cause);
    }
}