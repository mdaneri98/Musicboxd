package ar.edu.itba.paw.services.mappers;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.dtos.CommentDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {

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

    public Comment toEntity(CommentDTO dto) {
        if (dto == null) {
            return null;
        }

        Comment comment = new Comment();
        comment.setId(dto.getId());

        comment.setContent(dto.getContent());
        comment.setCreatedAt(dto.getCreatedAt());

        return comment;
    }

    public List<Comment> toEntityList(List<CommentDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}

