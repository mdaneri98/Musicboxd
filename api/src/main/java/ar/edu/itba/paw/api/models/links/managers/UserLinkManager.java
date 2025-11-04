package ar.edu.itba.paw.api.models.links.managers;

import ar.edu.itba.paw.api.models.resources.Resource;
import ar.edu.itba.paw.models.dtos.UserDTO;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.HATEOASUtils;
import ar.edu.itba.paw.api.utils.UriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ar.edu.itba.paw.api.utils.ControllerUtils;

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
        resource.addLink(uriBuilder.buildUserReviewsUri(baseUrl, userId), ControllerUtils.RELATION_REVIEWS, "User reviews", ControllerUtils.METHOD_GET);
        resource.addLink(uriBuilder.buildUserFollowersUri(baseUrl, userId), ControllerUtils.RELATION_FOLLOWERS, "User followers", ControllerUtils.METHOD_GET);
        resource.addLink(uriBuilder.buildUserFollowersUri(baseUrl, userId), ControllerUtils.RELATION_FOLLOWERS, "Follow user", ControllerUtils.METHOD_POST);
        resource.addLink(uriBuilder.buildUserFollowersUri(baseUrl, userId), ControllerUtils.RELATION_FOLLOWERS, "Unfollow user", ControllerUtils.METHOD_DELETE);
        resource.addLink(uriBuilder.buildUserFollowingUri(baseUrl, userId), ControllerUtils.RELATION_FOLLOWINGS, "User followings", ControllerUtils.METHOD_GET);
        resource.addLink(uriBuilder.buildUserFavoriteArtistsUri(baseUrl, userId), ControllerUtils.RELATION_FAVORITE_ARTISTS, "User favorite artists", ControllerUtils.METHOD_GET);
        resource.addLink(uriBuilder.buildUserFavoriteAlbumsUri(baseUrl, userId), ControllerUtils.RELATION_FAVORITE_ALBUMS, "User favorite albums", ControllerUtils.METHOD_GET);
        resource.addLink(uriBuilder.buildUserFavoriteSongsUri(baseUrl, userId), ControllerUtils.RELATION_FAVORITE_SONGS, "User favorite songs", ControllerUtils.METHOD_GET);
    }
}
