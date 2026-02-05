package ar.edu.itba.paw.webapp.mapper.dto;

import ar.edu.itba.paw.webapp.dto.ReviewDTO;
import ar.edu.itba.paw.webapp.dto.links.ReviewLinksDTO;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.ReviewType;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper to convert between Review model and ReviewDTO
 */
@Component
public class ReviewDtoMapper {

    public ReviewDTO toDTO(Review review, UriInfo uriInfo) {
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
        dto.setCommentAmount(review.getCommentAmount() != null ? review.getCommentAmount() : 0);
        dto.setItemType(review.getItemType());
        dto.setItemId(review.getItemId());
        dto.setItemName(review.getItemName());
        dto.setItemImageId(review.getItemImage() != null ? review.getItemImage().getId() : null);
        dto.setUserModerator(review.getUser() != null ? review.getUser().getModerator() : null);
        dto.setUserVerified(review.getUser() != null ? review.getUser().getVerified() : null);

        // Build HATEOAS links
        if (uriInfo != null) {
            ReviewLinksDTO links = new ReviewLinksDTO();

            links.setSelf(uriInfo.getBaseUriBuilder()
                    .path("reviews").path(String.valueOf(review.getId())).build());

            if (review.getUser() != null) {
                links.setUser(uriInfo.getBaseUriBuilder()
                        .path("users").path(String.valueOf(review.getUser().getId())).build());
            }

            // Build item link based on type
            if (review.getItemType() != null && review.getItemId() != null) {
                String itemPath = getItemPath(review.getItemType());
                links.setItem(uriInfo.getBaseUriBuilder()
                        .path(itemPath).path(String.valueOf(review.getItemId())).build());
            }

            links.setComments(uriInfo.getBaseUriBuilder()
                    .path("reviews").path(String.valueOf(review.getId())).path("comments").build());

            links.setLikes(uriInfo.getBaseUriBuilder()
                    .path("reviews").path(String.valueOf(review.getId())).path("likes").build());

            dto.setLinks(links);
        }

        return dto;
    }

    private String getItemPath(ReviewType itemType) {
        switch (itemType) {
            case ARTIST:
                return "artists";
            case ALBUM:
                return "albums";
            case SONG:
                return "songs";
            default:
                return "items";
        }
    }

    public List<ReviewDTO> toDTOList(List<Review> reviews, UriInfo uriInfo) {
        if (reviews == null) {
            return null;
        }

        return reviews.stream()
                .map(r -> toDTO(r, uriInfo))
                .collect(Collectors.toList());
    }
}
