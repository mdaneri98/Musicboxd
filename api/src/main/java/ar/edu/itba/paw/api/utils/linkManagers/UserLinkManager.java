package ar.edu.itba.paw.api.utils.linkManagers;

import ar.edu.itba.paw.api.models.Resource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.HATEOASUtils;
import ar.edu.itba.paw.api.utils.UriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Link manager for User resources using HATEOASUtils for common operations
 */
@Component
public class UserLinkManager {
    
    @Autowired
    private UriBuilder uriBuilder;
    
    public void addUserLinks(Resource<?> resource, String baseUrl, Long userId) {
        HATEOASUtils.addCrudLinks(resource, baseUrl, ApiUriConstants.USERS_BASE, userId);
        resource.addLink(uriBuilder.buildUserReviewsUri(baseUrl, userId), "reviews", "User reviews", "GET");
        resource.addLink(uriBuilder.buildUserFollowersUri(baseUrl, userId), "followers", "User followers", "GET");
        resource.addLink(uriBuilder.buildUserFollowingUri(baseUrl, userId), "following", "User following", "GET");
    }
}
