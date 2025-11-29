package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.FilterType;

import java.util.List;

public interface CommentService extends CrudService<Comment> {
    List<Comment> findByReviewId(Long reviewId, Integer pageNum, Integer pageSize);
    void updateReviewCommentAmount(Long reviewId);
    Long countByReviewId(Long reviewId);
    List<Comment> findPaginated(FilterType filter, Integer page, Integer pageSize);
    Long countAll();
    List<Comment> findBySubstring(String substring, Integer page, Integer size);
}
