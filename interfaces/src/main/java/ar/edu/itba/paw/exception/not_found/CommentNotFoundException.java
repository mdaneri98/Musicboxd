package ar.edu.itba.paw.exception.not_found;

public class CommentNotFoundException extends EntityNotFoundException {
    public CommentNotFoundException(Long id) {
        super("exception.CommentNotFound");
    }

    public CommentNotFoundException(Long id, Throwable cause) {
        super("exception.CommentNotFound", cause);
    }
}
