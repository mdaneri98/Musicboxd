package ar.edu.itba.paw.models.dtos;

import ar.edu.itba.paw.models.Image;

public class ImageDTO {
    private Long id;
    private Image image;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
