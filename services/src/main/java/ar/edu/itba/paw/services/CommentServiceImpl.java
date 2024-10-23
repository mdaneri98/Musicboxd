package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.persistence.CommentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;

    @Autowired
    public CommentServiceImpl(final CommentDao commentDao) {
        this.commentDao = commentDao;
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
        return commentDao.save(comment);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        commentDao.deleteById(id);
    }
}
