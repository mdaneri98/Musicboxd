package ar.edu.itba.paw.api.models.resources;

import ar.edu.itba.paw.models.Image;

public class ImageResource {
    private Image image;

    public ImageResource(Image image) {
        this.image = image;
    }
    
    
    public Long getId() {
        return image.getId();
    }
    
    public byte[] getBytes() {
        return image.getBytes();
    }

    public Image getImage() {
        return image;
    }
}
