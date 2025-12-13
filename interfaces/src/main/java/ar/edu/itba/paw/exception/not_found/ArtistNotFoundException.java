package ar.edu.itba.paw.exception.not_found;

public class ArtistNotFoundException extends EntityNotFoundException {
    public ArtistNotFoundException(Long id) {
        super("exception.ArtistNotFound");
    }

    public ArtistNotFoundException(Long id, Throwable cause) {
        super("exception.ArtistNotFound", cause);
    }
}
