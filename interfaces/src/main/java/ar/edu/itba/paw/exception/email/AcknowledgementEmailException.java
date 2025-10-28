package ar.edu.itba.paw.exception.email;

public class AcknowledgementEmailException extends EmailException {
    public AcknowledgementEmailException(String email) {
        super("Failed to send acknowledgement email to user: " + email);
    }

    public AcknowledgementEmailException(String email, Throwable cause) {
        super("Failed to send acknowledgement email to user: " + email, cause);
    }
}