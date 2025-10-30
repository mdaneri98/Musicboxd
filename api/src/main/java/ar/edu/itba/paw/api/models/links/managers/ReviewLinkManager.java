package ar.edu.itba.paw.api.models.links.managers;

import ar.edu.itba.paw.api.models.resources.Resource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.HATEOASUtils;
import ar.edu.itba.paw.api.utils.UriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
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
        resource.addLink(uriBuilder.buildReviewCommentsUri(baseUrl, reviewId), "comments", "Review comments", "GET");
        resource.addLink(uriBuilder.buildReviewLikesUri(baseUrl, reviewId), "likes", "Review likes", "GET");
        resource.addLink(uriBuilder.buildReviewCommentsUri(baseUrl, reviewId), "comments", "Create comment", "POST");
        resource.addLink(uriBuilder.buildReviewCommentsUri(baseUrl, reviewId), "comments", "Update comment", "PUT");
        resource.addLink(uriBuilder.buildReviewCommentsUri(baseUrl, reviewId), "comments", "Delete comment", "DELETE");
        resource.addLink(uriBuilder.buildReviewUri(baseUrl, reviewId), "block", "Block/Unblock review", "PATCH");
    }
}
