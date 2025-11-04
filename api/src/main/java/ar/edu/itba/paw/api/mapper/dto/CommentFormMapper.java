package ar.edu.itba.paw.api.mapper.dto;

import ar.edu.itba.paw.api.form.CommentForm;
import ar.edu.itba.paw.models.dtos.CommentDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert CommentForm to CommentDTO
 */
@Component
public class CommentFormMapper {

    public CommentDTO toDTO(CommentForm form) {
        if (form == null) {
            return null;
        }
        
        CommentDTO dto = new CommentDTO();
        dto.setContent(form.getContent());
        
        return dto;
    }
}

