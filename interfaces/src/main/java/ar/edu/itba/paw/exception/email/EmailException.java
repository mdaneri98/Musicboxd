package ar.edu.itba.paw.exception.email;

public class EmailException extends RuntimeException {
    public EmailException(String message) {
        super("exception.EmailSendFailed");
    }

    public EmailException(String message, Throwable cause) {
        super("exception.EmailSendFailed", cause);
    }
}
