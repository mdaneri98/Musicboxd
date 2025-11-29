package ar.edu.itba.paw.api.models.resources;

import ar.edu.itba.paw.api.dto.CommentDTO;

/**
 * HATEOAS resource wrapper for Comment entities
 */
public class CommentResource extends Resource<CommentDTO> {

    private final CommentDTO item;

    public CommentResource(CommentDTO item) {
        this.item = item;
    }

    @Override
    public CommentDTO getData() {
        return item;
    }
}
