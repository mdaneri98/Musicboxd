package ar.edu.itba.paw.api.utils.linkManagers;

import ar.edu.itba.paw.api.models.Resource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.HATEOASUtils;
import ar.edu.itba.paw.api.utils.UriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Link manager for Song resources using HATEOASUtils for common operations
 */
@Component
public class SongLinkManager {
    
    @Autowired
    private UriBuilder uriBuilder;
    
    public void addSongLinks(Resource<?> resource, String baseUrl, Long songId) {
        HATEOASUtils.addCrudLinks(resource, baseUrl, ApiUriConstants.SONGS_BASE, songId);
        resource.addLink(uriBuilder.buildSongReviewsUri(baseUrl, songId), "reviews", "Song reviews", "GET");
        
    }
}

