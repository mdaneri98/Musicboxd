package ar.edu.itba.paw.api.mapper.dto;

import ar.edu.itba.paw.api.form.ReviewForm;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
import org.springframework.stereotype.Component;
import ar.edu.itba.paw.models.reviews.ReviewType;

/**
 * Mapper to convert ReviewForm to ReviewDTO
 */
@Component
public class ReviewFormMapper {

    public ReviewDTO toDTO(ReviewForm form) {
        if (form == null) {
            return null;
        }
        
        ReviewDTO dto = new ReviewDTO();
        dto.setTitle(form.getTitle());
        dto.setDescription(form.getDescription());
        dto.setRating(form.getRating());
        dto.setItemId(form.getItemId().longValue());
        dto.setItemType(ReviewType.valueOf(form.getItemType()));
        return dto;
    }
}

