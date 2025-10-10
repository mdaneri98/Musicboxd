package ar.edu.itba.paw.services.exception;

public class AlbumNotFoundException extends RuntimeException {
    public AlbumNotFoundException(String message) {
        super(message);
    }

    public AlbumNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
