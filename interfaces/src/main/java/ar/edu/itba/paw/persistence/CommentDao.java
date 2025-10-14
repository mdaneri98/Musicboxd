package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentDao {
    Optional<Comment> findById(Long id);
    List<Comment> findByReviewId(Long reviewId, Integer pageSize, Integer pageNum);
    Comment save(Comment comment);
    Void deleteById(Long id);
    Long countByReviewId(Long reviewId);
}
