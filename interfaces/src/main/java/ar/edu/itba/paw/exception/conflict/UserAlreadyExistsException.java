package ar.edu.itba.paw.exception.conflict;

public class UserAlreadyExistsException extends ConflictException {
    public UserAlreadyExistsException(String messageCode) {
        super(messageCode);
    }

    public UserAlreadyExistsException(String messageCode, Throwable cause) {
        super(messageCode, cause);
    }
}