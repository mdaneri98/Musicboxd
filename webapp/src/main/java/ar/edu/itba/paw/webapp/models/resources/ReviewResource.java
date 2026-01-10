package ar.edu.itba.paw.webapp.models.resources;

import ar.edu.itba.paw.webapp.dto.ReviewDTO;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * HATEOAS resource wrapper for Review DTOs
 */
public class ReviewResource extends Resource<ReviewDTO> {

    @JsonUnwrapped
    private final ReviewDTO data;

    public ReviewResource(ReviewDTO data) {
        this.data = data;
    }
}
