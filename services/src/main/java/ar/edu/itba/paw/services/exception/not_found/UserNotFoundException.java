package ar.edu.itba.paw.services.exception.not_found;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(Long id) {
        super("User with id " + id + " not found");
    }
    
    public UserNotFoundException(Long id, Throwable cause) {
        super("User with id " + id + " not found", cause);
    }

    public UserNotFoundException(String content, String type) {
        super("User with " + type + " " + content + " not found");
    }

    public UserNotFoundException(String content, String type, Throwable cause) {
        super("User with " + type + " " + content + " not found", cause);
    }
}