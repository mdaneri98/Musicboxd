package ar.edu.itba.paw.exception.not_found;

public class NotificationNotFoundException extends EntityNotFoundException {
    public NotificationNotFoundException(Long id) {
        super("exception.NotificationNotFound");
    }

    public NotificationNotFoundException(Long id, Throwable cause) {
        super("exception.NotificationNotFound", cause);
    }
}

