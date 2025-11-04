package ar.edu.itba.paw.api.mapper.dto;

import ar.edu.itba.paw.api.form.ReviewForm;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
import org.springframework.stereotype.Component;

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
        
        return dto;
    }
}

