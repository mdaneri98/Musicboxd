package ar.edu.itba.paw.api.utils.linkManagers;

import ar.edu.itba.paw.api.models.Resource;
import ar.edu.itba.paw.api.utils.UriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReviewLinkManager {
    
    @Autowired
    private UriBuilder uriBuilder;
    
    public void addReviewLinks(Resource<?> resource, String baseUrl, Long reviewId) {
        resource.addSelfLink(uriBuilder.buildReviewUri(baseUrl, reviewId));
        resource.addEditLink(uriBuilder.buildReviewUri(baseUrl, reviewId));
        resource.addDeleteLink(uriBuilder.buildReviewUri(baseUrl, reviewId));
        resource.addLink("comments", uriBuilder.buildReviewCommentsUri(baseUrl, reviewId));
        resource.addLink("likes", uriBuilder.buildReviewLikesUri(baseUrl, reviewId));
        resource.addCollectionLink(uriBuilder.buildCollectionUri(baseUrl, "reviews"));
    }
    
    public void addReviewActionLinks(Resource<?> resource, String baseUrl, Long reviewId) {
        resource.addLink(uriBuilder.buildReviewLikesUri(baseUrl, reviewId), "like", "Like this review", null, "POST");
        resource.addLink(uriBuilder.buildReviewLikesUri(baseUrl, reviewId), "unlike", "Unlike this review", null, "DELETE");
    }
    
    public void addReviewCollectionLinks(Resource<?> resource, String baseUrl) {
        resource.addSelfLink(uriBuilder.buildCollectionUri(baseUrl, "reviews"));
        resource.addCreateLink(uriBuilder.buildCollectionUri(baseUrl, "reviews"));
    }
}

