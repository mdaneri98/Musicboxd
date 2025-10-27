package ar.edu.itba.paw.exception;

public class AcknowledgementEmailException extends RuntimeException {
    public AcknowledgementEmailException(String email) {
        super("Failed to send acknowledgement email to user: " + email);
    }

    public AcknowledgementEmailException(String email, Throwable cause) {
        super("Failed to send acknowledgement email to user: " + email, cause);
    }
}