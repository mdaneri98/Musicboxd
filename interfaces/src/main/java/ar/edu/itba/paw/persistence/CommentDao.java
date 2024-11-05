package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentDao {
    Optional<Comment> findById(long id);
    List<Comment> findByReviewId(long reviewId, int pageSize, int offset);
    Comment save(Comment comment);
    void deleteById(long id);
}
