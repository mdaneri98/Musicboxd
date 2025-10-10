package ar.edu.itba.paw.api.utils.linkManagers;

import ar.edu.itba.paw.api.models.Resource;
import ar.edu.itba.paw.api.utils.UriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentLinkManager {
    
    @Autowired
    private UriBuilder uriBuilder;
    
    public void addCommentLinks(Resource<?> resource, String baseUrl, Long commentId) {
        resource.addSelfLink(uriBuilder.buildCommentUri(baseUrl, commentId));
        resource.addEditLink(uriBuilder.buildCommentUri(baseUrl, commentId));
        resource.addDeleteLink(uriBuilder.buildCommentUri(baseUrl, commentId));
        resource.addCollectionLink(uriBuilder.buildCollectionUri(baseUrl, "comments"));
    }
    
    public void addCommentCollectionLinks(Resource<?> resource, String baseUrl) {
        resource.addSelfLink(uriBuilder.buildCollectionUri(baseUrl, "comments"));
        resource.addCreateLink(uriBuilder.buildCollectionUri(baseUrl, "comments"));
    }
}

