package ar.edu.itba.paw.services.exception;

public class VerificationEmailException extends RuntimeException {
    public VerificationEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}