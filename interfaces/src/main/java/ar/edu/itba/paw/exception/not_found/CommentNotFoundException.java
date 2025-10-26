package ar.edu.itba.paw.exception.not_found;

public class CommentNotFoundException extends EntityNotFoundException {
    public CommentNotFoundException(Long id) {
        super("Comment with id " + id + " not found");
    }
    
    public CommentNotFoundException(Long id, Throwable cause) {
        super("Comment with id " + id + " not found", cause);
    }
}
