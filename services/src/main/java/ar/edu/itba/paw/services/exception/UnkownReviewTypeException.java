package ar.edu.itba.paw.services.exception;

public class UnkownReviewTypeException extends RuntimeException {
    public UnkownReviewTypeException(String message) {
        super(message);
    }

    public UnkownReviewTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
