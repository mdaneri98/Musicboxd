package ar.edu.itba.paw.services.exception;

public class VerificationEmailException extends RuntimeException {

    public VerificationEmailException(String email) {
        super("Failed to send verification email to user: " + email);
    }

    public VerificationEmailException(String email, Throwable cause) {
        super("Failed to send verification email to user: " + email, cause);
    }
}