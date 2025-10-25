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
    }
    
    public void addReviewActionLinks(Resource<?> resource, String baseUrl, Long reviewId) {
        resource.addLink(uriBuilder.buildReviewLikesUri(baseUrl, reviewId), "like", "Like this review", "POST");
        resource.addLink(uriBuilder.buildReviewLikesUri(baseUrl, reviewId), "unlike", "Unlike this review", "DELETE");
    }
}
