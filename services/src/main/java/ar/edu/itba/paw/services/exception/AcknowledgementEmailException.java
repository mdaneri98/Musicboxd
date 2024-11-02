package ar.edu.itba.paw.services.exception;

public class AcknowledgementEmailException extends RuntimeException {
    public AcknowledgementEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}