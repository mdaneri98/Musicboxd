package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.persistence.CommentDao;
import ar.edu.itba.paw.services.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ar.edu.itba.paw.persistence.ReviewDao;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;
    private final ReviewDao reviewDao;
    private final TimeUtils timeUtils;

    @Autowired
    public CommentServiceImpl(final CommentDao commentDao, final ReviewDao reviewDao, final MessageSource messageSource) {
        this.commentDao = commentDao;
        this.reviewDao = reviewDao;
        this.timeUtils = new TimeUtils(messageSource);
    }

    @Override
    public Optional<Comment> findById(long id) {
        return commentDao.findById(id);
    }

    @Override
    public List<Comment> findByReviewId(long reviewId) {
        List<Comment> comments = commentDao.findByReviewId(reviewId);
        setTimeAgo(comments);
        return comments;
    }

    @Override
    @Transactional
    public Comment save(Comment comment) {
        Comment savedComment = commentDao.save(comment);
        updateReviewCommentAmount(comment.getReview().getId());
        return savedComment;
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        Optional<Comment> comment = commentDao.findById(id);
        if (comment.isPresent()) {
            commentDao.deleteById(id);
            updateReviewCommentAmount(comment.get().getReview().getId());
        }
    }

    @Override
    @Transactional
    public void updateReviewCommentAmount(long reviewId) {
        reviewDao.updateCommentAmount(reviewId);
    }

    private void setTimeAgo(List<Comment> comments) {
        for (Comment comment : comments) {
            comment.setTimeAgo(timeUtils.formatTimeAgo(comment.getCreatedAt()));
        }
    }
}
