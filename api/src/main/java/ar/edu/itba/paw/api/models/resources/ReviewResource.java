package ar.edu.itba.paw.api.models.resources;

import ar.edu.itba.paw.api.dto.ReviewDTO;

/**
 * HATEOAS resource wrapper for Review DTOs
 */
public class ReviewResource extends Resource<ReviewDTO> {

    private final ReviewDTO data;

    public ReviewResource(ReviewDTO data) {
        this.data = data;
    }

    @Override
    public ReviewDTO getData() {
        return data;
    }
}
