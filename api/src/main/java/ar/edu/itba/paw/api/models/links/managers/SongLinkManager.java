package ar.edu.itba.paw.api.models.links.managers;

import ar.edu.itba.paw.api.models.resources.Resource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.HATEOASUtils;
import ar.edu.itba.paw.api.utils.UriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ar.edu.itba.paw.models.dtos.SongDTO;
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
        resource.addLink(uriBuilder.buildSongAlbumUri(baseUrl, resource.getData().getAlbumId()), "album", "Song album", "GET");
        resource.addLink(uriBuilder.buildSongArtistUri(baseUrl, resource.getData().getArtistId()), "artist", "Song artist", "GET");
        resource.addLink(uriBuilder.buildSongReviewsUri(baseUrl, songId), "reviews", "Song reviews", "GET");
        resource.addLink(uriBuilder.buildSongReviewsUri(baseUrl, songId), "reviews", "Create Song review", "POST");
        resource.addLink(uriBuilder.buildSongReviewsUri(baseUrl, songId), "reviews", "Update Song review", "PUT");
    }
}

