package ar.edu.itba.paw.webapp.models.links.managers;

import ar.edu.itba.paw.webapp.models.resources.Resource;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.models.Image;
import org.springframework.stereotype.Component;

/**
 * Link manager for Image resources
 */
@Component
public class ImageLinkManager {
    
    public void addImageLinks(Resource<Image> resource, String baseUrl, Long imageId) {
        String imageUrl = baseUrl + ApiUriConstants.IMAGES_BASE + "/" + imageId;
        resource.addSelfLink(imageUrl);
    }
}

