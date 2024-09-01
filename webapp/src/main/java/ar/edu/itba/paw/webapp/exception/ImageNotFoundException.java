package ar.edu.itba.paw.webapp.exception;

public class ImageNotFoundException extends ResourceNotFoundException {
    public ImageNotFoundException() {
        super("exception.ImageNotFoundException");
    }
}