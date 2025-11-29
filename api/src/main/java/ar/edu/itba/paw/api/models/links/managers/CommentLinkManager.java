package ar.edu.itba.paw.api.models.links.managers;

import ar.edu.itba.paw.api.dto.CommentDTO;
import ar.edu.itba.paw.api.models.resources.Resource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.HATEOASUtils;
import ar.edu.itba.paw.api.utils.UriBuilder;
import ar.edu.itba.paw.api.utils.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentLinkManager {

    @Autowired
    private UriBuilder uriBuilder;

    public void addCommentLinks(Resource<CommentDTO> resource, String baseUrl, Long commentId) {
        HATEOASUtils.addCrudLinks(resource, baseUrl, ApiUriConstants.COMMENTS_BASE, commentId);
        resource.addLink(uriBuilder.buildCommentReviewUri(baseUrl, resource.getData().getReviewId()), ControllerUtils.RELATION_REVIEW, "Comment in review", ControllerUtils.METHOD_GET);
    }
}
