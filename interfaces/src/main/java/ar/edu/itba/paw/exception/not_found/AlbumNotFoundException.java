package ar.edu.itba.paw.exception.not_found;

public class AlbumNotFoundException extends EntityNotFoundException {
    public AlbumNotFoundException(Long id) {
        super("exception.AlbumNotFound");
    }

    public AlbumNotFoundException(Long id, Throwable cause) {
        super("exception.AlbumNotFound", cause);
    }
}
