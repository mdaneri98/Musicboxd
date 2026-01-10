package ar.edu.itba.paw.webapp.models.resources;

import ar.edu.itba.paw.webapp.dto.CommentDTO;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * HATEOAS resource wrapper for Comment entities
 */
public class CommentResource extends Resource<CommentDTO> {

    @JsonUnwrapped
    private final CommentDTO item;

    public CommentResource(CommentDTO item) {
        this.item = item;
    }
}
