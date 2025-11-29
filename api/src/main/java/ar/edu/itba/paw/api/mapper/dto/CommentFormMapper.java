package ar.edu.itba.paw.api.mapper.dto;

import ar.edu.itba.paw.api.form.CommentForm;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;
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
            };
            review.setId(form.getReviewId());
            comment.setReview(review);
        }
        
        if (form.getUserId() != null) {
            User user = new User();
            user.setId(form.getUserId());
            comment.setUser(user);
        }
        
        return comment;
    }

    public Comment toModel(CommentForm form, Long userId, Long reviewId) {
        Comment comment = toModel(form);
        if (comment != null) {
            if (userId != null) {
                User user = new User();
                user.setId(userId);
                comment.setUser(user);
            }
            if (reviewId != null) {
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
                };
                review.setId(reviewId);
                comment.setReview(review);
            }
        }
        return comment;
    }
}
