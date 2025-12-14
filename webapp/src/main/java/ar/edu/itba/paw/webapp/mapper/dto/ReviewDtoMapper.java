package ar.edu.itba.paw.webapp.mapper.dto;

import ar.edu.itba.paw.webapp.dto.ReviewDTO;
import ar.edu.itba.paw.models.reviews.Review;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper to convert between Review model and ReviewDTO
 */
@Component
public class ReviewDtoMapper {

    public ReviewDTO toDTO(Review review) {
        if (review == null) {
            return null;
        }

        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setUserId(review.getUser() != null ? review.getUser().getId() : null);
        dto.setUserImageId(review.getUser() != null ? review.getUser().getImageId() : null);
        dto.setUsername(review.getUser() != null ? review.getUser().getUsername() : null);
        dto.setTitle(review.getTitle());
        dto.setDescription(review.getDescription());
        dto.setRating(review.getRating());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setLikes(review.getLikes());
        dto.setIsBlocked(review.isBlocked());
        dto.setCommentAmount(review.getCommentAmount());
        dto.setItemType(review.getItemType());
        dto.setItemId(review.getItemId());
        dto.setItemName(review.getItemName());
        dto.setItemImageId(review.getItemImage() != null ? review.getItemImage().getId() : null);
        dto.setUserModerator(review.getUser() != null ? review.getUser().getModerator() : null);
        dto.setUserVerified(review.getUser() != null ? review.getUser().getVerified() : null);
        dto.setIsLiked(review.getIsLiked());
        return dto;
    }

    public List<ReviewDTO> toDTOList(List<Review> reviews) {
        if (reviews == null) {
            return null;
        }

        return reviews.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}

