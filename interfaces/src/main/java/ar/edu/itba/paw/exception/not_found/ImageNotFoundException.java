package ar.edu.itba.paw.exception.not_found;

public class ImageNotFoundException extends EntityNotFoundException {
    public ImageNotFoundException(Long id) {
        super("exception.ImageNotFound");
    }

    public ImageNotFoundException(Long id, Throwable cause) {
        super("exception.ImageNotFound", cause);
    }
}
