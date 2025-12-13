package ar.edu.itba.paw.exception;

public class UnkownReviewTypeException extends RuntimeException {
    public UnkownReviewTypeException(String type) {
        super("exception.UnknownReviewType");
    }

    public UnkownReviewTypeException(String type, Throwable cause) {
        super("exception.UnknownReviewType", cause);
    }
}
