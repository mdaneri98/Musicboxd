package ar.edu.itba.paw.webapp.mapper.resource;

import ar.edu.itba.paw.webapp.models.resources.ImageResource;
import ar.edu.itba.paw.webapp.models.links.managers.ImageLinkManager;
import ar.edu.itba.paw.models.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ImageResourceMapper implements ResourceMapper<Image, ImageResource> {

    @Autowired
    private ImageLinkManager imageLinkManager;

    @Override
    public ImageResource toResource(Image image, String baseUrl) {
        ImageResource resource = new ImageResource(image);
        imageLinkManager.addImageLinks(resource, baseUrl, image.getId());
        return resource;
    }

    @Override
    public List<ImageResource> toResourceList(List<Image> images, String baseUrl) {
        return images.stream()
                .map(image -> toResource(image, baseUrl))
                .collect(Collectors.toList());
    }
}
