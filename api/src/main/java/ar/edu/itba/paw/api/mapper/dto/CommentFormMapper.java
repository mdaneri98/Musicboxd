package ar.edu.itba.paw.api.mapper.dto;

import ar.edu.itba.paw.api.form.CommentForm;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.ReviewType;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert CommentForm to Comment model
 */
@Component
public class CommentFormMapper {

    public Comment toModel(CommentForm form) {
        if (form == null) {
            return null;
        }
        
        Comment comment = new Comment();
        comment.setContent(form.getContent());
        
        if (form.getReviewId() != null) {
            comment.setReview(createReviewStub(form.getReviewId()));
        }
        
        if (form.getUserId() != null) {
            User user = new User();
            user.setId(form.getUserId());
            comment.setUser(user);
        }
        
        return comment;
    }

    public Comment toModel(CommentForm form, Long userId) {
        Comment comment = toModel(form);
        if (comment != null) {
            if (userId != null) {
                User user = new User();
                user.setId(userId);
                comment.setUser(user);
            }
        }
        return comment;
    }

    /**
     * Creates a Review stub with only the ID set.
     * This is used when we only need to reference a Review by ID.
     */
    private Review createReviewStub(Long reviewId) {
        Review review = new Review() {
            @Override
            public Long getItemId() {
                return null;
            }
            @Override
            public String getItemName() {
                return null;
            }
            @Override
            public ar.edu.itba.paw.models.Image getItemImage() {
                return null;
            }
            @Override
            public ReviewType getItemType() {
                return null;
            }
        };
        review.setId(reviewId);
        return review;
    }
}
