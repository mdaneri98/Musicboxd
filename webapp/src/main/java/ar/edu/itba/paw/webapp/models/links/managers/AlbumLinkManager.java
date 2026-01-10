package ar.edu.itba.paw.webapp.models.links.managers;

import ar.edu.itba.paw.webapp.dto.AlbumDTO;
import ar.edu.itba.paw.webapp.models.resources.Resource;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.HATEOASUtils;
import ar.edu.itba.paw.webapp.utils.UriBuilder;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Link manager for Album resources using HATEOASUtils for common operations
 */
@Component
public class AlbumLinkManager {
    
    @Autowired
    private UriBuilder uriBuilder;
    
    public void addAlbumLinks(Resource<AlbumDTO> resource, String baseUrl, Long albumId, AlbumDTO dto) {
        HATEOASUtils.addCrudLinks(resource, baseUrl, ApiUriConstants.ALBUMS_BASE, albumId);
        HATEOASUtils.addImageLinks(resource, baseUrl, ApiUriConstants.ALBUMS_BASE, dto.getImageId());
        resource.addLink(uriBuilder.buildAlbumReviewsUri(baseUrl, albumId), ControllerUtils.RELATION_REVIEWS, "Album reviews", ControllerUtils.METHOD_GET);
        resource.addLink(uriBuilder.buildAlbumReviewsUri(baseUrl, albumId), ControllerUtils.RELATION_REVIEWS, "Create Album review", ControllerUtils.METHOD_POST);
        resource.addLink(uriBuilder.buildAlbumArtistUri(baseUrl, dto.getArtistId()), ControllerUtils.RELATION_ARTIST, "Album artist", ControllerUtils.METHOD_GET);
        resource.addLink(uriBuilder.buildAlbumSongsUri(baseUrl, albumId), ControllerUtils.RELATION_SONGS, "Album songs", ControllerUtils.METHOD_GET );
        resource.addLink(uriBuilder.buildAlbumSongsUri(baseUrl, albumId), ControllerUtils.RELATION_SONGS, "Create Album song", ControllerUtils.METHOD_POST);
        resource.addLink(uriBuilder.buildAlbumFavoriteUri(baseUrl, albumId), ControllerUtils.RELATION_FAVORITE, "Album favorite", ControllerUtils.METHOD_POST);
        resource.addLink(uriBuilder.buildAlbumFavoriteUri(baseUrl, albumId), ControllerUtils.RELATION_FAVORITE, "Album favorite", ControllerUtils.METHOD_DELETE);
    }
}
