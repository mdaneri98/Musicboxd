package ar.edu.itba.paw.api.models.links.managers;

import ar.edu.itba.paw.api.models.resources.Resource;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.HATEOASUtils;
import ar.edu.itba.paw.api.utils.UriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Link manager for Artist resources using HATEOASUtils for common operations
 */
@Component
public class ArtistLinkManager {
    
    @Autowired
    private UriBuilder uriBuilder;
    
    public void addArtistLinks(Resource<ArtistDTO> resource, String baseUrl, Long artistId) {
        HATEOASUtils.addCrudLinks(resource, baseUrl, ApiUriConstants.ARTISTS_BASE, artistId);
        HATEOASUtils.addImageLinks(resource, baseUrl, ApiUriConstants.ARTISTS_BASE, resource.getData().getImageId());
        resource.addLink(uriBuilder.buildArtistReviewsUri(baseUrl, artistId), "reviews", "Artist reviews", "GET");
        resource.addLink(uriBuilder.buildArtistReviewsUri(baseUrl, artistId), "reviews", "Create Artist review", "POST");
        resource.addLink(uriBuilder.buildArtistReviewsUri(baseUrl, artistId), "reviews", "Update Artist review", "PUT");
        resource.addLink(uriBuilder.buildArtistAlbumsUri(baseUrl, artistId), "albums", "Artist albums", "GET");
        resource.addLink(uriBuilder.buildArtistAlbumsUri(baseUrl, artistId), "albums", "Create Artist album", "POST");
        resource.addLink(uriBuilder.buildArtistFavoriteUri(baseUrl, artistId), "favorite", "Artist favorite", "POST");
        resource.addLink(uriBuilder.buildArtistFavoriteUri(baseUrl, artistId), "favorite", "Artist favorite", "DELETE");
    }
}

