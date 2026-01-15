package ar.edu.itba.paw.webapp.mapper.dto;

import ar.edu.itba.paw.webapp.dto.CommentDTO;
import ar.edu.itba.paw.models.Comment;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper to convert between Comment model and CommentDTO
 */
@Component
public class CommentDtoMapper {

    public CommentDTO toDTO(Comment comment, UriInfo uriInfo) {
        if (comment == null) {
            return null;
        }

        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setUserId(comment.getUser() != null ? comment.getUser().getId() : null);
        dto.setUsername(comment.getUser() != null ? comment.getUser().getUsername() : null);
        dto.setUserImageId(comment.getUser() != null ? comment.getUser().getImageId() : null);
        dto.setReviewId(comment.getReview() != null ? comment.getReview().getId() : null);
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUserModerator(comment.getUser() != null ? comment.getUser().getModerator() : null);
        dto.setUserVerified(comment.getUser() != null ? comment.getUser().getVerified() : null);

        // Build HATEOAS links
        if (uriInfo != null) {
            dto.setSelf(uriInfo.getBaseUriBuilder()
                    .path("comments").path(String.valueOf(comment.getId())).build());

            if (comment.getUser() != null) {
                dto.setUserLink(uriInfo.getBaseUriBuilder()
                        .path("users").path(String.valueOf(comment.getUser().getId())).build());
            }

            if (comment.getReview() != null) {
                dto.setReviewLink(uriInfo.getBaseUriBuilder()
                        .path("reviews").path(String.valueOf(comment.getReview().getId())).build());
            }
        }

        return dto;
    }

    public List<CommentDTO> toDTOList(List<Comment> comments, UriInfo uriInfo) {
        if (comments == null) {
            return null;
        }

        return comments.stream()
                .map(c -> toDTO(c, uriInfo))
                .collect(Collectors.toList());
    }

    public Comment toModel(CommentDTO dto) {
        if (dto == null) {
            return null;
        }

        Comment comment = new Comment();
        comment.setId(dto.getId());
        comment.setContent(dto.getContent());
        comment.setCreatedAt(dto.getCreatedAt());

        return comment;
    }
}
