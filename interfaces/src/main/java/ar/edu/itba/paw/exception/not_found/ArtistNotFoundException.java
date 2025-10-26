package ar.edu.itba.paw.exception.not_found;

public class ArtistNotFoundException extends EntityNotFoundException {
    public ArtistNotFoundException(Long id) {
        super("Artist with id " + id + " not found");
    }

    public ArtistNotFoundException(Long id, Throwable cause) {
        super("Artist with id " + id + " not found", cause);
    }
}
