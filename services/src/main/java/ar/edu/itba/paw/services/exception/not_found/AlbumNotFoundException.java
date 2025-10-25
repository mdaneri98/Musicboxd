package ar.edu.itba.paw.services.exception.not_found;

public class AlbumNotFoundException extends EntityNotFoundException {
    public AlbumNotFoundException(Long id) {
        super("Album with id " + id + " not found");
    }

    public AlbumNotFoundException(Long id, Throwable cause) {
        super("Album with id " + id + " not found", cause);
    }
}
