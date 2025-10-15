package ar.edu.itba.paw.api.mapper;

import ar.edu.itba.paw.api.resources.ImageResource;
import ar.edu.itba.paw.models.Image;
import java.util.List;
import java.util.stream.Collectors;

public class ImageResourceMapper {

    public ImageResource toResource(Image image) {
        return new ImageResource(image);
    }

    public List<ImageResource> toResourceList(List<Image> images) {
        return images.stream()
                .map(this::toResource)
                .collect(Collectors.toList());
    }
}
