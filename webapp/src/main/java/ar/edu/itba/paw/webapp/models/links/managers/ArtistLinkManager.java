package ar.edu.itba.paw.webapp.models.links.managers;

import ar.edu.itba.paw.webapp.dto.ArtistDTO;
import ar.edu.itba.paw.webapp.models.resources.Resource;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.HATEOASUtils;
import ar.edu.itba.paw.webapp.utils.UriBuilder;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Link manager for Artist resources using HATEOASUtils for common operations
 */
@Component
public class ArtistLinkManager {
    
    @Autowired
    private UriBuilder uriBuilder;
    
    public void addArtistLinks(Resource<ArtistDTO> resource, String baseUrl, Long artistId, ArtistDTO dto) {
        HATEOASUtils.addCrudLinks(resource, baseUrl, ApiUriConstants.ARTISTS_BASE, artistId);
        HATEOASUtils.addImageLinks(resource, baseUrl, ApiUriConstants.ARTISTS_BASE, dto.getImageId());
        resource.addLink(uriBuilder.buildArtistReviewsUri(baseUrl, artistId), ControllerUtils.RELATION_REVIEWS, "Artist reviews", ControllerUtils.METHOD_GET);
        resource.addLink(uriBuilder.buildArtistReviewsUri(baseUrl, artistId), ControllerUtils.RELATION_REVIEWS, "Create Artist review", ControllerUtils.METHOD_POST);
        resource.addLink(uriBuilder.buildArtistSongsUri(baseUrl, artistId), ControllerUtils.RELATION_SONGS, "Artist songs", ControllerUtils.METHOD_GET);
        resource.addLink(uriBuilder.buildArtistAlbumsUri(baseUrl, artistId), ControllerUtils.RELATION_ALBUMS, "Artist albums", ControllerUtils.METHOD_GET);
        resource.addLink(uriBuilder.buildArtistAlbumsUri(baseUrl, artistId), ControllerUtils.RELATION_ALBUMS, "Create Artist album", ControllerUtils.METHOD_POST);
        resource.addLink(uriBuilder.buildArtistFavoriteUri(baseUrl, artistId), ControllerUtils.RELATION_FAVORITE, "Artist favorite", ControllerUtils.METHOD_POST);
        resource.addLink(uriBuilder.buildArtistFavoriteUri(baseUrl, artistId), ControllerUtils.RELATION_FAVORITE, "Artist favorite", ControllerUtils.METHOD_DELETE);
    }
}
