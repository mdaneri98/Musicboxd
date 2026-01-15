package ar.edu.itba.paw.webapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;

/**
 * DTO for API root information
 */
public class ApiInfoDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("version")
    private String version;

    @JsonProperty("description")
    private String description;

    // HATEOAS links
    private URI users;
    private URI artists;
    private URI albums;
    private URI songs;
    private URI reviews;

    public ApiInfoDTO() {
    }

    public ApiInfoDTO(String name, String version, String description) {
        this.name = name;
        this.version = version;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // HATEOAS getters and setters
    public URI getUsers() {
        return users;
    }

    public void setUsers(URI users) {
        this.users = users;
    }

    public URI getArtists() {
        return artists;
    }

    public void setArtists(URI artists) {
        this.artists = artists;
    }

    public URI getAlbums() {
        return albums;
    }

    public void setAlbums(URI albums) {
        this.albums = albums;
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
