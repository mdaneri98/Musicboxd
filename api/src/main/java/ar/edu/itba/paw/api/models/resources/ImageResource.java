package ar.edu.itba.paw.api.models.resources;

import ar.edu.itba.paw.models.Image;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * HATEOAS resource wrapper for Image entities
 */
public class ImageResource extends Resource<Image> {

    private final Image item;

    public ImageResource(Image item) {
        super(item);
        this.item = item;
    }

    @Override
    public Image getData() {
        return item;
    }
    
    /**
     * Expose only the ID in the JSON response
     */
    @JsonProperty("id")
    public Long getId() {
        return item.getId();
    }
}
