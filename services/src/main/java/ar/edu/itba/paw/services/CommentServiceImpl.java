package ar.edu.itba.paw.services;

import ar.edu.itba.paw.domain.user.User;
import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.domain.user.UserRepository;
import ar.edu.itba.paw.persistence.mappers.LegacyUserMapper;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.persistence.CommentDao;
import ar.edu.itba.paw.persistence.ReviewDao;
import ar.edu.itba.paw.exception.not_found.CommentNotFoundException;
import ar.edu.itba.paw.exception.not_found.UserNotFoundException;
import ar.edu.itba.paw.exception.not_found.ReviewNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;
    private final ReviewDao reviewDao;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final LegacyUserMapper legacyUserMapper;

    @Autowired
    public CommentServiceImpl(final CommentDao commentDao, final ReviewDao reviewDao, final NotificationService notificationService, final UserRepository userRepository, final LegacyUserMapper legacyUserMapper) {
        this.commentDao = commentDao;
        this.reviewDao = reviewDao;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.legacyUserMapper = legacyUserMapper;
    }

    @Override
    public List<Comment> findAll() {
        List<Comment> comments = commentDao.findAll();
        return comments;
    }

    @Override
    public List<Comment> findPaginated(FilterType filterType, Integer page, Integer pageSize) {
        List<Comment> comments = commentDao.findPaginated(filterType, page, pageSize);
        return comments;
    }

    @Override
    @Transactional
    public Comment update(Comment commentInput) {
        Comment comment = commentDao.findById(commentInput.getId()).orElseThrow(() -> new CommentNotFoundException(commentInput.getId()));
        
        if (commentInput.getContent() != null) {
            comment.setContent(commentInput.getContent());
        }
        
        Comment updatedComment = commentDao.update(comment);
        User user = legacyUserMapper.toDomain(comment.getUser());
        Review review = comment.getReview();
        notificationService.notifyComment(review, user);
        return updatedComment;
    }

    @Override
    public Comment findById(Long id) {
        Comment comment = commentDao.findById(id).orElseThrow(() -> new CommentNotFoundException(id));
        return comment;
    }

    @Override
    public List<Comment> findByReviewId(Long reviewId, Integer pageNum, Integer pageSize) {
        List<Comment> comments = commentDao.findByReviewId(reviewId, pageNum, pageSize);
        return comments;
    }

    @Override
    @Transactional
    public Comment create(Comment commentInput) {
        if (commentInput.getUser() == null || commentInput.getUser().getId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (commentInput.getReview() == null || commentInput.getReview().getId() == null) {
            throw new IllegalArgumentException("Review ID is required");
        }

        User user = userRepository.findById(new UserId(commentInput.getUser().getId()))
                .orElseThrow(() -> new UserNotFoundException(commentInput.getUser().getId()));
        Review review = reviewDao.findById(commentInput.getReview().getId())
                .orElseThrow(() -> new ReviewNotFoundException(commentInput.getReview().getId()));

        Comment comment = new Comment();
        comment.setUser(legacyUserMapper.toJpaEntity(user));
        comment.setReview(review);
        comment.setContent(commentInput.getContent());
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentDao.create(comment);

        updateReviewCommentAmount(review.getId());
        notificationService.notifyComment(review, user);
        return savedComment;
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

    @Override
    public Long countByReviewId(Long reviewId) {
        return commentDao.countByReviewId(reviewId);
    }

    @Override
    public Long countAll() {
        return commentDao.countAll();
    }

    @Override
    public List<Comment> findBySubstring(String substring, Integer page, Integer size) {
        return commentDao.findBySubstring(substring, page, size);
    }
}
