package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Comment;
import java.util.List;

public interface CommentDao extends CrudDao<Comment> {
    List<Comment> findByReviewId(Long reviewId, Integer pageSize, Integer pageNum);
    Long countByReviewId(Long reviewId);
    Long countAll();
    List<Comment> findBySubstring(String substring, Integer page, Integer size);
}
