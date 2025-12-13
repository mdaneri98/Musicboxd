package ar.edu.itba.paw.exception.not_found;

public class SongNotFoundException extends EntityNotFoundException {
    public SongNotFoundException(Long id) {
        super("exception.SongNotFound");
    }

    public SongNotFoundException(Long id, Throwable cause) {
        super("exception.SongNotFound", cause);
    }
}
