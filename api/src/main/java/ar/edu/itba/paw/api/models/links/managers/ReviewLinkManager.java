package ar.edu.itba.paw.api.models.links.managers;

import ar.edu.itba.paw.api.dto.ReviewDTO;
import ar.edu.itba.paw.api.models.resources.Resource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.HATEOASUtils;
import ar.edu.itba.paw.api.utils.UriBuilder;
import ar.edu.itba.paw.api.utils.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Link manager for Review resources using HATEOASUtils for common operations
 */
@Component
public class ReviewLinkManager {
    
    @Autowired
    private UriBuilder uriBuilder;
    
    public void addReviewLinks(Resource<ReviewDTO> resource, String baseUrl, Long reviewId) {
        HATEOASUtils.addCrudLinks(resource, baseUrl, ApiUriConstants.REVIEWS_BASE, reviewId);
        HATEOASUtils.addImageLinks(resource, baseUrl, ApiUriConstants.REVIEWS_BASE, resource.getData().getItemImageId());
        resource.addLink(uriBuilder.buildReviewCommentsUri(baseUrl, reviewId), ControllerUtils.RELATION_COMMENTS, "Review comments", ControllerUtils.METHOD_GET);
        resource.addLink(uriBuilder.buildReviewLikesUri(baseUrl, reviewId), ControllerUtils.RELATION_LIKES, "Review likes", ControllerUtils.METHOD_GET);
        resource.addLink(uriBuilder.buildReviewLikesUri(baseUrl, reviewId), ControllerUtils.RELATION_LIKES, "Like review", ControllerUtils.METHOD_POST);
        resource.addLink(uriBuilder.buildReviewLikesUri(baseUrl, reviewId), ControllerUtils.RELATION_LIKES, "Unlike review", ControllerUtils.METHOD_DELETE);   
        resource.addLink(uriBuilder.buildReviewCommentsUri(baseUrl, reviewId), ControllerUtils.RELATION_COMMENTS, "Create comment", ControllerUtils.METHOD_POST);
        resource.addLink(uriBuilder.buildReviewCommentsUri(baseUrl, reviewId), ControllerUtils.RELATION_COMMENTS, "Update comment", ControllerUtils.METHOD_PUT);
        resource.addLink(uriBuilder.buildReviewCommentsUri(baseUrl, reviewId), ControllerUtils.RELATION_COMMENTS, "Delete comment", ControllerUtils.METHOD_DELETE);
        resource.addLink(uriBuilder.buildReviewUri(baseUrl, reviewId), ControllerUtils.RELATION_BLOCK, "Block/Unblock review", ControllerUtils.METHOD_PATCH);
    }
}
