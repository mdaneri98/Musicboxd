package ar.edu.itba.paw.exception;

public class UnkownReviewTypeException extends RuntimeException {
    public UnkownReviewTypeException(String type) {
        super("Unknown review type: " + type);
    }

    public UnkownReviewTypeException(String type, Throwable cause) {
        super("Unknown review type: " + type, cause);
    }
}
