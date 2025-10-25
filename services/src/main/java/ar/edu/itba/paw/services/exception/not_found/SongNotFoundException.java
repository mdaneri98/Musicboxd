package ar.edu.itba.paw.services.exception.not_found;

public class SongNotFoundException extends EntityNotFoundException {
    public SongNotFoundException(Long id) {
        super("Song with id " + id + " not found");
    }

    public SongNotFoundException(Long id, Throwable cause) {
        super("Song with id " + id + " not found", cause);
    }
}
