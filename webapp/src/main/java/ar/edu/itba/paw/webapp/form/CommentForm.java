package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Size;

public class CommentForm {
    
    @Size(max = 255, message = "{validation.comment.content.size}")
    @Size(min = 2, message = "{validation.comment.content.size}")
    private String content;

    public CommentForm() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
