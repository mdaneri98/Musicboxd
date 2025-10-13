package ar.edu.itba.paw.api.utils.linkManagers;

import ar.edu.itba.paw.api.models.Resource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.HATEOASUtils;
import ar.edu.itba.paw.api.utils.UriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Link manager for Review resources using HATEOASUtils for common operations
 */
@Component
public class ReviewLinkManager {
    
    @Autowired
    private UriBuilder uriBuilder;
    
    public void addReviewLinks(Resource<?> resource, String baseUrl, Long reviewId) {
        HATEOASUtils.addCrudLinks(resource, baseUrl, ApiUriConstants.REVIEWS_BASE, reviewId);
        resource.addLink(uriBuilder.buildReviewCommentsUri(baseUrl, reviewId), "comments", "Review comments", "GET");
        resource.addLink(uriBuilder.buildReviewLikesUri(baseUrl, reviewId), "likes", "Review likes", "GET");
    }
    
    public void addReviewActionLinks(Resource<?> resource, String baseUrl, Long reviewId) {
        resource.addLink(uriBuilder.buildReviewLikesUri(baseUrl, reviewId), "like", "Like this review", "POST");
        resource.addLink(uriBuilder.buildReviewLikesUri(baseUrl, reviewId), "unlike", "Unlike this review", "DELETE");
    }
}
