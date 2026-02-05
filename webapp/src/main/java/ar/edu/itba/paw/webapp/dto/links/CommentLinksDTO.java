package ar.edu.itba.paw.webapp.dto.links;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.net.URI;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentLinksDTO {

    private URI self;
    private URI user;
    private URI review;

    public CommentLinksDTO() {
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getUser() {
        return user;
    }

    public void setUser(URI user) {
        this.user = user;
    }

    public URI getReview() {
        return review;
    }

    public void setReview(URI review) {
        this.review = review;
    }
}
