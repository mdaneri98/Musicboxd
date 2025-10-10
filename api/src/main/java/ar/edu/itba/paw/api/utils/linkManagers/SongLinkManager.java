package ar.edu.itba.paw.api.utils.linkManagers;

import ar.edu.itba.paw.api.models.Resource;
import ar.edu.itba.paw.api.utils.UriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SongLinkManager {
    
    @Autowired
    private UriBuilder uriBuilder;
    
    public void addSongLinks(Resource<?> resource, String baseUrl, Long songId) {
        resource.addSelfLink(uriBuilder.buildSongUri(baseUrl, songId));
        resource.addEditLink(uriBuilder.buildSongUri(baseUrl, songId));
        resource.addDeleteLink(uriBuilder.buildSongUri(baseUrl, songId));
        resource.addLink("reviews", uriBuilder.buildSongReviewsUri(baseUrl, songId));
        resource.addCollectionLink(uriBuilder.buildCollectionUri(baseUrl, "songs"));
    }
    
    public void addSongCollectionLinks(Resource<?> resource, String baseUrl) {
        resource.addSelfLink(uriBuilder.buildCollectionUri(baseUrl, "songs"));
        resource.addCreateLink(uriBuilder.buildCollectionUri(baseUrl, "songs"));
        resource.addLink("search", uriBuilder.buildSearchUri(baseUrl, "songs", ""));
    }
}

