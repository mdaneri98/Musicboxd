package ar.edu.itba.paw.api.models.links.managers;

import ar.edu.itba.paw.api.models.resources.Resource;
import ar.edu.itba.paw.models.dtos.UserDTO;
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
    
    public void addUserLinks(Resource<UserDTO> resource, String baseUrl, Long userId) {
        HATEOASUtils.addCrudLinks(resource, baseUrl, ApiUriConstants.USERS_BASE, userId);
        HATEOASUtils.addImageLinks(resource, baseUrl, ApiUriConstants.USERS_BASE, resource.getData().getImageId());
        resource.addLink(uriBuilder.buildUserReviewsUri(baseUrl, userId), "reviews", "User reviews", "GET");
        resource.addLink(uriBuilder.buildUserFollowersUri(baseUrl, userId), "followers", "User followers", "GET");
        resource.addLink(uriBuilder.buildUserFollowingUri(baseUrl, userId), "followings", "User followings", "GET");
        resource.addLink(uriBuilder.buildUserFollowingUri(baseUrl, userId), "followings", "User followings", "GET");
        resource.addLink(uriBuilder.buildUserFavoriteArtistsUri(baseUrl, userId), "favorite-artists", "User favorite artists", "GET");
        resource.addLink(uriBuilder.buildUserFavoriteAlbumsUri(baseUrl, userId), "favorite-albums", "User favorite albums", "GET");
        resource.addLink(uriBuilder.buildUserFavoriteSongsUri(baseUrl, userId), "favorite-songs", "User favorite songs", "GET");
    }
}
