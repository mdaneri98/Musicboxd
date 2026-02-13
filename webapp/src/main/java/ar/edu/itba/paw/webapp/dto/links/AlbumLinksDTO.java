package ar.edu.itba.paw.webapp.dto.links;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.net.URI;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlbumLinksDTO {

    private URI self;
    private URI image;
    private URI artist;
    private URI songs;
    private URI reviews;

    public AlbumLinksDTO() {
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getImage() {
        return image;
    }

    public void setImage(URI image) {
        this.image = image;
    }

    public URI getArtist() {
        return artist;
    }

    public void setArtist(URI artist) {
        this.artist = artist;
    }

    public URI getSongs() {
        return songs;
    }

    public void setSongs(URI songs) {
        this.songs = songs;
    }

    public URI getReviews() {
        return reviews;
    }

    public void setReviews(URI reviews) {
        this.reviews = reviews;
    }
}
