package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.persistence.CommentDao;
import ar.edu.itba.paw.services.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ar.edu.itba.paw.persistence.ReviewDao;
import java.util.List;
import ar.edu.itba.paw.services.mappers.CommentMapper;
import ar.edu.itba.paw.models.dtos.CommentDTO;
import ar.edu.itba.paw.services.exception.CommentNotFoundException;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.services.exception.UserNotFoundException;
import ar.edu.itba.paw.services.exception.ReviewNotFoundException;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.FilterType;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;
    private final ReviewDao reviewDao;
    private final UserDao userDao;
    private final NotificationService notificationService;
    private final CommentMapper commentMapper;

    @Autowired
    public CommentServiceImpl(final CommentDao commentDao, final ReviewDao reviewDao, final NotificationService notificationService, final CommentMapper commentMapper, final UserDao userDao) {
        this.commentDao = commentDao;
        this.reviewDao = reviewDao;
        this.notificationService = notificationService;
        this.commentMapper = commentMapper;
        this.userDao = userDao;
    }

    @Override
    public List<CommentDTO> findAll() {
        throw new RuntimeException("Method not allowed");
    }

    @Override
    public List<CommentDTO> findPaginated(FilterType filterType, int page, int pageSize) {
        throw new RuntimeException("Method not allowed");
    }

    @Override
    public CommentDTO update(CommentDTO commentDTO) {
        throw new RuntimeException("Method not allowed");
    }

    @Override
    public CommentDTO findById(Long id) {
        return commentMapper.toDTO(commentDao.findById(id).orElseThrow(() -> new CommentNotFoundException("Comment with id " + id + " not found")));
    }

    @Override
    public List<CommentDTO> findByReviewId(long reviewId, int pageSize, int pageNum) {
        int offset = (pageNum - 1) * pageSize;
        List<Comment> comments = commentDao.findByReviewId(reviewId, pageSize, offset);
        setTimeAgo(comments);
        return commentMapper.toDTOList(comments);
    }

    @Override
    @Transactional
    public CommentDTO create(CommentDTO commentDTO) {
        Comment savedComment = commentDao.save(commentMapper.toEntity(commentDTO));
        updateReviewCommentAmount(commentDTO.getReviewId());
        User user = userDao.findById(commentDTO.getUserId()).orElseThrow(() -> new UserNotFoundException("User with id " + commentDTO.getUserId() + " not found"));
        Review review = reviewDao.findById(commentDTO.getReviewId().longValue()).orElseThrow(() -> new ReviewNotFoundException("Review with id " + commentDTO.getReviewId() + " not found"));
        notificationService.notifyComment(review, user);
        return commentMapper.toDTO(savedComment);
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        Comment comment = commentDao.findById(id).orElseThrow(() -> new CommentNotFoundException("Comment with id " + id + " not found"));
        commentDao.deleteById(id);
        updateReviewCommentAmount(comment.getReview().getId());
        return true;
    }

    @Override
    @Transactional
    public void updateReviewCommentAmount(long reviewId) {
        reviewDao.updateCommentAmount(reviewId);
    }

    private void setTimeAgo(List<Comment> comments) {
        for (Comment comment : comments) {
            comment.setTimeAgo(TimeUtils.formatTimeAgo(comment.getCreatedAt()));
        }
    }
}
