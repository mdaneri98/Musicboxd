package ar.edu.itba.paw.services.exception.conflict;

public class UserAlreadyExistsException extends ConflictException {
    public UserAlreadyExistsException(String content, String type) {
        super("User with " + type + " " + content + " already exists");
    }

    public UserAlreadyExistsException(String content, String type, Throwable cause) {
        super("User with " + type + " " + content + " already exists", cause);
    }
}