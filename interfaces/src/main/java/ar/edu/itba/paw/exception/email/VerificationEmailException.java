package ar.edu.itba.paw.exception.email;

public class VerificationEmailException extends EmailException {

    public VerificationEmailException(String email) {
        super("Failed to send verification email to user: " + email);
    }

    public VerificationEmailException(String email, Throwable cause) {
        super("Failed to send verification email to user: " + email, cause);
    }
}