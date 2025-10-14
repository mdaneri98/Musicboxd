package ar.edu.itba.paw.api.utils.linkManagers;

import ar.edu.itba.paw.api.models.Resource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.HATEOASUtils;
import ar.edu.itba.paw.api.utils.UriBuilder;
import ar.edu.itba.paw.models.dtos.AlbumDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Link manager for Album resources using HATEOASUtils for common operations
 */
@Component
public class AlbumLinkManager {
    
    @Autowired
    private UriBuilder uriBuilder;
    
    public void addAlbumLinks(Resource<AlbumDTO> resource, String baseUrl, Long albumId) {
        HATEOASUtils.addCrudLinks(resource, baseUrl, ApiUriConstants.ALBUMS_BASE, albumId);
        HATEOASUtils.addImageLinks(resource, baseUrl, ApiUriConstants.ALBUMS_BASE, resource.getData().getImageId());
        resource.addLink(uriBuilder.buildAlbumReviewsUri(baseUrl, albumId), "reviews", "Album reviews", "GET");
        resource.addLink(uriBuilder.buildAlbumArtistUri(baseUrl, resource.getData().getArtistId()), "artist", "Album artist", "GET");
        resource.addLink(uriBuilder.buildAlbumReviewsUri(baseUrl, albumId), "reviews", "Create Album review", "POST");
        resource.addLink(uriBuilder.buildAlbumReviewsUri(baseUrl, albumId), "reviews", "Update Album review", "PUT");
        resource.addLink(uriBuilder.buildAlbumSongsUri(baseUrl, albumId), "songs", "Album songs", "GET" );
        resource.addLink(uriBuilder.buildAlbumSongsUri(baseUrl, albumId), "songs", "Create Album song", "POST");
    }
}

