package ar.edu.itba.paw.exception.email;

public class VerificationEmailException extends EmailException {
    public VerificationEmailException(String email) {
        super("exception.VerificationEmailFailed");
    }

    public VerificationEmailException(String email, Throwable cause) {
        super("exception.VerificationEmailFailed", cause);
    }
}