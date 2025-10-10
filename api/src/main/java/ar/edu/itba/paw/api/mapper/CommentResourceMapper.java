package ar.edu.itba.paw.api.mapper;

import ar.edu.itba.paw.api.models.CommentResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.HATEOASUtils;
import ar.edu.itba.paw.models.dtos.CommentDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentResourceMapper implements ResourceMapper<CommentDTO, CommentResource> {

    @Override
    public CommentResource toResource(CommentDTO commentDTO, String baseUrl) {
        CommentResource resource = new CommentResource(commentDTO);
        
        // Add CRUD links
        HATEOASUtils.addCrudLinks(resource, baseUrl, ApiUriConstants.COMMENTS_BASE, commentDTO.getId());
        
        // Add related resources links
        if (commentDTO.getUserId() != null) {
            resource.addLink(baseUrl + ApiUriConstants.USERS_BASE + "/" + commentDTO.getUserId(), 
                            "user", "Comment author");
        }
        
        if (commentDTO.getReviewId() != null) {
            resource.addLink(baseUrl + ApiUriConstants.REVIEWS_BASE + "/" + commentDTO.getReviewId(), 
                            "review", "Parent review");
        }
        
        if (commentDTO.getUserImageId() != null) {
            resource.addLink(baseUrl + ApiUriConstants.IMAGES_BASE + "/" + commentDTO.getUserImageId(), 
                            "userImage", "Author image");
        }
        
        return resource;
    }

    @Override
    public List<CommentResource> toResourceList(List<CommentDTO> commentDTOs, String baseUrl) {
        return commentDTOs.stream()
                .map(dto -> toResource(dto, baseUrl))
                .collect(Collectors.toList());
    }
}

