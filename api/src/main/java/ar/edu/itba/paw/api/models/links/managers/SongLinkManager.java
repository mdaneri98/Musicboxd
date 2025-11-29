package ar.edu.itba.paw.api.models.links.managers;

import ar.edu.itba.paw.api.dto.SongDTO;
import ar.edu.itba.paw.api.models.resources.Resource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.HATEOASUtils;
import ar.edu.itba.paw.api.utils.UriBuilder;
import ar.edu.itba.paw.api.utils.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Link manager for Song resources using HATEOASUtils for common operations
 */
@Component
public class SongLinkManager {
    
    @Autowired
    private UriBuilder uriBuilder;
    
    public void addSongLinks(Resource<SongDTO> resource, String baseUrl, Long songId) {
        HATEOASUtils.addCrudLinks(resource, baseUrl, ApiUriConstants.SONGS_BASE, songId);
        HATEOASUtils.addImageLinks(resource, baseUrl, ApiUriConstants.SONGS_BASE, resource.getData().getAlbumImageId());
        resource.addLink(uriBuilder.buildSongAlbumUri(baseUrl, resource.getData().getAlbumId()), ControllerUtils.RELATION_ALBUM, "Song album", ControllerUtils.METHOD_GET);
        resource.addLink(uriBuilder.buildSongArtistUri(baseUrl, resource.getData().getArtistId()), ControllerUtils.RELATION_ARTIST, "Song artist", ControllerUtils.METHOD_GET);
        resource.addLink(uriBuilder.buildSongReviewsUri(baseUrl, songId), ControllerUtils.RELATION_REVIEWS, "Song reviews", ControllerUtils.METHOD_GET);
        resource.addLink(uriBuilder.buildSongReviewsUri(baseUrl, songId), ControllerUtils.RELATION_REVIEWS, "Create song review", ControllerUtils.METHOD_POST);
        resource.addLink(uriBuilder.buildSongFavoriteUri(baseUrl, songId), ControllerUtils.RELATION_FAVORITE, "Add song to favorites", ControllerUtils.METHOD_POST);
        resource.addLink(uriBuilder.buildSongFavoriteUri(baseUrl, songId), ControllerUtils.RELATION_FAVORITE, "Remove song from favorites", ControllerUtils.METHOD_DELETE);
    }
}
