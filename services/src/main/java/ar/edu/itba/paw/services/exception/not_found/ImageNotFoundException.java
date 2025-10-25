package ar.edu.itba.paw.services.exception.not_found;

public class ImageNotFoundException extends EntityNotFoundException {
    public ImageNotFoundException(Long id) {
        super("Image with id " + id + " not found");
    }
 
    public ImageNotFoundException(Long id, Throwable cause) {
        super("Image with id " + id + " not found", cause);
    }
}
