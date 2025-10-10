package ar.edu.itba.paw.api.utils.linkManagers;

import ar.edu.itba.paw.api.models.Resource;
import ar.edu.itba.paw.api.utils.UriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AlbumLinkManager {
    
    @Autowired
    private UriBuilder uriBuilder;
    
    public void addAlbumLinks(Resource<?> resource, String baseUrl, Long albumId) {
        resource.addSelfLink(uriBuilder.buildAlbumUri(baseUrl, albumId));
        resource.addEditLink(uriBuilder.buildAlbumUri(baseUrl, albumId));
        resource.addDeleteLink(uriBuilder.buildAlbumUri(baseUrl, albumId));
        resource.addLink("reviews", uriBuilder.buildAlbumReviewsUri(baseUrl, albumId));
        resource.addLink("songs", uriBuilder.buildAlbumSongsUri(baseUrl, albumId));
        resource.addCollectionLink(uriBuilder.buildCollectionUri(baseUrl, "albums"));
    }
    
    public void addAlbumCollectionLinks(Resource<?> resource, String baseUrl) {
        resource.addSelfLink(uriBuilder.buildCollectionUri(baseUrl, "albums"));
        resource.addCreateLink(uriBuilder.buildCollectionUri(baseUrl, "albums"));
        resource.addLink("search", uriBuilder.buildSearchUri(baseUrl, "albums", ""));
    }
}

