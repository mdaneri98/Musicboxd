package ar.edu.itba.paw.services;

import java.util.List;
import ar.edu.itba.paw.models.dtos.CommentDTO;

public interface CommentService extends CrudService<CommentDTO> {
    List<CommentDTO> findByReviewId(Long reviewId, Integer pageSize, Integer pageNum);
    void updateReviewCommentAmount(Long reviewId);
}
