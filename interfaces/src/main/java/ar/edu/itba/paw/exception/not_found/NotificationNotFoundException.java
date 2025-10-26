package ar.edu.itba.paw.exception.not_found;

public class NotificationNotFoundException extends EntityNotFoundException {
    public NotificationNotFoundException(Long id) {
        super("Notification with id " + id + " not found");
    }

    public NotificationNotFoundException(Long id, Throwable cause) {
        super("Notification with id " + id + " not found", cause);
    }
}

