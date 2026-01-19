package ar.edu.itba.paw.webapp.dto.links;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.net.URI;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageLinksDTO {

    private URI self;

    public ImageLinksDTO() {
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}
