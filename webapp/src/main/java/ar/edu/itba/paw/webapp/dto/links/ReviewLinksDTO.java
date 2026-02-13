package ar.edu.itba.paw.webapp.dto.links;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.net.URI;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewLinksDTO {

    private URI self;
    private URI user;
    private URI item;
    private URI comments;
    private URI likes;

    public ReviewLinksDTO() {
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

    public URI getItem() {
        return item;
    }

    public void setItem(URI item) {
        this.item = item;
    }

    public URI getComments() {
        return comments;
    }

    public void setComments(URI comments) {
        this.comments = comments;
    }

    public URI getLikes() {
        return likes;
    }

    public void setLikes(URI likes) {
        this.likes = likes;
    }
}
