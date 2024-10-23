package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<Comment> findById(long id);
    List<Comment> findByReviewId(long reviewId);
    Comment save(Comment comment);
    void deleteById(long id);
}
