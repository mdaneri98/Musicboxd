package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.persistence.CommentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ar.edu.itba.paw.persistence.ReviewDao;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;
    private final ReviewDao reviewDao;

    @Autowired
    public CommentServiceImpl(final CommentDao commentDao, final ReviewDao reviewDao) {
        this.commentDao = commentDao;
        this.reviewDao = reviewDao;
    }

    @Override
    public Optional<Comment> findById(long id) {
        return commentDao.findById(id);
    }

    @Override
    public List<Comment> findByReviewId(long reviewId) {
        return commentDao.findByReviewId(reviewId);
    }

    @Override
    @Transactional
    public Comment save(Comment comment) {
        Comment savedComment = commentDao.save(comment);
        updateReviewCommentAmount(comment.getReviewId());
        return savedComment;
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        Optional<Comment> comment = commentDao.findById(id);
        if (comment.isPresent()) {
            commentDao.deleteById(id);
            updateReviewCommentAmount(comment.get().getReviewId());
        }
    }

    @Override
    @Transactional
    public void updateReviewCommentAmount(long reviewId) {
        reviewDao.updateCommentAmount(reviewId);
    }
}
