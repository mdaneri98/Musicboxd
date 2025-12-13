package ar.edu.itba.paw.exception.email;

public class AcknowledgementEmailException extends EmailException {
    public AcknowledgementEmailException(String email) {
        super("exception.AcknowledgementEmailFailed");
    }

    public AcknowledgementEmailException(String email, Throwable cause) {
        super("exception.AcknowledgementEmailFailed", cause);
    }
}