package ar.edu.itba.paw.services.mappers;

import ar.edu.itba.paw.models.dtos.ReviewDTO;
import ar.edu.itba.paw.models.reviews.Review;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewMapper {

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
        dto.setIsLiked(review.isLiked());
        dto.setIsBlocked(review.isBlocked());
        dto.setCommentAmount(review.getCommentAmount());
        dto.setItemType(review.getItemType());
        dto.setItemId(review.getItemId());
        dto.setItemName(review.getItemName());
        dto.setItemImageId(review.getItemImage().getId());
        dto.setTimeAgo(review.getTimeAgo());
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


    // Nota: No creamos toEntity porque Review es abstracta y necesitaríamos 
    // saber el tipo específico (AlbumReview, ArtistReview, SongReview) para instanciarla
}

