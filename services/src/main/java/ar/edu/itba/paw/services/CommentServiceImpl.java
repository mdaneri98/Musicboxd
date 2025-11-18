package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.persistence.CommentDao;
import ar.edu.itba.paw.services.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ar.edu.itba.paw.persistence.ReviewDao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import ar.edu.itba.paw.services.mappers.CommentMapper;
import ar.edu.itba.paw.models.dtos.CommentDTO;
import ar.edu.itba.paw.exception.not_found.CommentNotFoundException;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.exception.not_found.UserNotFoundException;
import ar.edu.itba.paw.exception.not_found.ReviewNotFoundException;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.services.utils.MergeUtils;

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
        List<Comment> comments = commentDao.findAll();
        setTimeAgo(comments);
        return  commentMapper.toDTOList(comments);
    }

    @Override
    public List<CommentDTO> findPaginated(FilterType filterType, Integer page, Integer pageSize) {
        return commentMapper.toDTOList(commentDao.findPaginated(filterType, page, pageSize));
    }

    @Override
    @Transactional
    public CommentDTO update(CommentDTO commentDTO) {
        Comment comment = commentDao.findById(commentDTO.getId()).orElseThrow(() -> new CommentNotFoundException(commentDTO.getId()));
        MergeUtils.mergeCommentFields(comment, commentDTO);
        CommentDTO updatedCommentDTO = commentMapper.toDTO(commentDao.update(comment));
        User user = userDao.findById(updatedCommentDTO.getUserId()).orElseThrow(() -> new UserNotFoundException(updatedCommentDTO.getUserId()));
        Review review = reviewDao.findById(updatedCommentDTO.getReviewId().longValue()).orElseThrow(() -> new ReviewNotFoundException(updatedCommentDTO.getReviewId()));
        notificationService.notifyComment(review, user);
        return updatedCommentDTO;
    }

    @Override
    public CommentDTO findById(Long id) {
        Comment comment = commentDao.findById(id).orElseThrow(() -> new CommentNotFoundException(id));
        comment.setTimeAgo(TimeUtils.formatTimeAgo(comment.getCreatedAt()));
        return commentMapper.toDTO(comment);
    }

    @Override
    public List<CommentDTO> findByReviewId(Long reviewId, Integer pageNum, Integer pageSize) {
        List<Comment> comments = commentDao.findByReviewId(reviewId, pageNum, pageSize);
        setTimeAgo(comments);
        return commentMapper.toDTOList(comments);
    }

    @Override
    @Transactional
    public CommentDTO create(CommentDTO commentDTO) {
        if (commentDTO.getUserId() == null || commentDTO.getReviewId() == null) throw new IllegalArgumentException("User ID and review ID are required");

        Comment createdComment = commentMapper.toEntity(commentDTO);

        User user = userDao.findById(commentDTO.getUserId()).orElseThrow(() -> new UserNotFoundException(commentDTO.getUserId()));
        Review review = reviewDao.findById(commentDTO.getReviewId().longValue()).orElseThrow(() -> new ReviewNotFoundException(commentDTO.getReviewId()));
        createdComment.setUser(user);
        createdComment.setReview(review);
        createdComment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentDao.create(createdComment);

        updateReviewCommentAmount(commentDTO.getReviewId());
        notificationService.notifyComment(review, user);
        return commentMapper.toDTO(savedComment);
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        Comment comment = commentDao.findById(id).orElseThrow(() -> new CommentNotFoundException(id));
        Boolean deleted = commentDao.delete(id);
        updateReviewCommentAmount(comment.getReview().getId());
        return deleted;
    }

    @Override
    @Transactional
    public void updateReviewCommentAmount(Long reviewId) {
        reviewDao.updateCommentAmount(reviewId);
    }

    private void setTimeAgo(List<Comment> comments) {
        for (Comment comment : comments) {
            comment.setTimeAgo(TimeUtils.formatTimeAgo(comment.getCreatedAt()));
        }
    }

    @Override
    public Long countByReviewId(Long reviewId) {
        return commentDao.countByReviewId(reviewId);
    }

    @Override
    public Long countAll() {
        return commentDao.countAll();
    }

    @Override
    public List<CommentDTO> findBySubstring(String substring, Integer page, Integer size) {
        List<Comment> comments = commentDao.findBySubstring(substring, page, size);
        setTimeAgo(comments);
        return commentMapper.toDTOList(comments);
    }
}
