package ar.edu.itba.paw.webapp.models.links.managers;

import ar.edu.itba.paw.webapp.dto.CommentDTO;
import ar.edu.itba.paw.webapp.models.resources.Resource;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.HATEOASUtils;
import ar.edu.itba.paw.webapp.utils.UriBuilder;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentLinkManager {

    @Autowired
    private UriBuilder uriBuilder;

    public void addCommentLinks(Resource<CommentDTO> resource, String baseUrl, Long commentId, CommentDTO dto) {
        HATEOASUtils.addCrudLinks(resource, baseUrl, ApiUriConstants.COMMENTS_BASE, commentId);
        resource.addLink(uriBuilder.buildCommentReviewUri(baseUrl, dto.getReviewId()), ControllerUtils.RELATION_REVIEW, "Comment in review", ControllerUtils.METHOD_GET);
    }
}
