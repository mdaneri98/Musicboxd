package ar.edu.itba.paw.api.utils;

import ar.edu.itba.paw.api.models.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserLinkManager {
    
    @Autowired
    private UriBuilder uriBuilder;
    
    public void addUserLinks(Resource<?> resource, String baseUrl, Long userId) {
        resource.addSelfLink(uriBuilder.buildUserUri(baseUrl, userId));
        resource.addLink("reviews", uriBuilder.buildUserReviewsUri(baseUrl, userId));
        resource.addLink("followers", uriBuilder.buildUserFollowersUri(baseUrl, userId));
        resource.addLink("following", uriBuilder.buildUserFollowingUri(baseUrl, userId));
        resource.addEditLink(uriBuilder.buildUserUri(baseUrl, userId));
        resource.addDeleteLink(uriBuilder.buildUserUri(baseUrl, userId));
    }
    
    public void addUserCollectionLinks(Resource<?> resource, String baseUrl) {
        resource.addSelfLink(uriBuilder.buildCollectionUri(baseUrl, "users"));
        resource.addCreateLink(uriBuilder.buildCollectionUri(baseUrl, "users"));
        resource.addLink("search", uriBuilder.buildSearchUri(baseUrl, "users", ""));
    }
}
