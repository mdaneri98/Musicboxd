package ar.edu.itba.paw.services;

import java.util.List;
import ar.edu.itba.paw.models.dtos.CommentDTO;
import ar.edu.itba.paw.models.FilterType;

public interface CommentService extends CrudService<CommentDTO> {
    List<CommentDTO> findByReviewId(Long reviewId, Integer pageSize, Integer pageNum);
    void updateReviewCommentAmount(Long reviewId);
    Long countByReviewId(Long reviewId);
    List<CommentDTO> findPaginated(FilterType filter, Integer page, Integer pageSize);
    Long countAll();
    List<CommentDTO> findBySubstring(String substring, Integer page, Integer size);
}
