package ar.edu.itba.paw.webapp.dto.links;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.net.URI;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationLinksDTO {

    private URI self;
    private URI recipientUser;
    private URI triggerUser;
    private URI review;

    public NotificationLinksDTO() {
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getRecipientUser() {
        return recipientUser;
    }

    public void setRecipientUser(URI recipientUser) {
        this.recipientUser = recipientUser;
    }

    public URI getTriggerUser() {
        return triggerUser;
    }

    public void setTriggerUser(URI triggerUser) {
        this.triggerUser = triggerUser;
    }

    public URI getReview() {
        return review;
    }

    public void setReview(URI review) {
        this.review = review;
    }
}
