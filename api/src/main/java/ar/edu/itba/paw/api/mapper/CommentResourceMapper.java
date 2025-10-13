package ar.edu.itba.paw.api.mapper;

import ar.edu.itba.paw.api.models.CommentResource;
import ar.edu.itba.paw.models.dtos.CommentDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentResourceMapper implements ResourceMapper<CommentDTO, CommentResource> {

    @Override
    public CommentResource toResource(CommentDTO commentDTO, String baseUrl) {
        CommentResource resource = new CommentResource(commentDTO);
        return resource;
    }

    @Override
    public List<CommentResource> toResourceList(List<CommentDTO> commentDTOs, String baseUrl) {
        return commentDTOs.stream()
                .map(dto -> toResource(dto, baseUrl))
                .collect(Collectors.toList());
    }
}

