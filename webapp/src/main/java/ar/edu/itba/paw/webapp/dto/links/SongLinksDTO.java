package ar.edu.itba.paw.webapp.dto.links;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.net.URI;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SongLinksDTO {

    private URI self;
    private URI album;
    private URI artist;
    private URI reviews;

    public SongLinksDTO() {
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getAlbum() {
        return album;
    }

    public void setAlbum(URI album) {
        this.album = album;
    }

    public URI getArtist() {
        return artist;
    }

    public void setArtist(URI artist) {
        this.artist = artist;
    }

    public URI getReviews() {
        return reviews;
    }

    public void setReviews(URI reviews) {
        this.reviews = reviews;
    }
}
