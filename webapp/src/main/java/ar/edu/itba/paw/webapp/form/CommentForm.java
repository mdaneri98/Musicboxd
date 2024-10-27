package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Size;

public class CommentForm {
    
    @Size(max = 500, message = "Comments can not exceed 500 characters")
    @Size(min = 2, message = "Comments must be at least 2 characters long")
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
