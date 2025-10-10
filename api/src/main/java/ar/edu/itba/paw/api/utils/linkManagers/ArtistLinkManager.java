package ar.edu.itba.paw.api.utils.linkManagers;

import ar.edu.itba.paw.api.models.Resource;
import ar.edu.itba.paw.api.utils.UriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArtistLinkManager {
    
    @Autowired
    private UriBuilder uriBuilder;
    
    public void addArtistLinks(Resource<?> resource, String baseUrl, Long artistId) {
        resource.addSelfLink(uriBuilder.buildArtistUri(baseUrl, artistId));
        resource.addEditLink(uriBuilder.buildArtistUri(baseUrl, artistId));
        resource.addDeleteLink(uriBuilder.buildArtistUri(baseUrl, artistId));
        resource.addLink("reviews", uriBuilder.buildArtistReviewsUri(baseUrl, artistId));
        resource.addCollectionLink(uriBuilder.buildCollectionUri(baseUrl, "artists"));
    }
    
    public void addArtistCollectionLinks(Resource<?> resource, String baseUrl) {
        resource.addSelfLink(uriBuilder.buildCollectionUri(baseUrl, "artists"));
        resource.addCreateLink(uriBuilder.buildCollectionUri(baseUrl, "artists"));
        resource.addLink("search", uriBuilder.buildSearchUri(baseUrl, "artists", ""));
    }
}

