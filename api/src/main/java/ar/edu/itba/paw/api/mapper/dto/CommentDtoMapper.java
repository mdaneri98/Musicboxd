package ar.edu.itba.paw.api.mapper.dto;

import ar.edu.itba.paw.api.dto.CommentDTO;
import ar.edu.itba.paw.models.Comment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper to convert between Comment model and CommentDTO
 */
@Component
public class CommentDtoMapper {

    public CommentDTO toDTO(Comment comment) {
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

        return dto;
    }

    public List<CommentDTO> toDTOList(List<Comment> comments) {
        if (comments == null) {
            return null;
        }

        return comments.stream()
                .map(this::toDTO)
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

