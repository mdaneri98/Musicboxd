package ar.edu.itba.paw.api.form;

import javax.validation.constraints.Size;

public class CommentForm {
    
    @Size(max = 255, message = "{validation.comment.content.size}")
    @Size(min = 2, message = "{validation.comment.content.size}")
    private String content;

    private Long reviewId;
    private Long userId;

    public CommentForm() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
