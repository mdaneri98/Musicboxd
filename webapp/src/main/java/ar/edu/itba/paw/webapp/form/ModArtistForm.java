package ar.edu.itba.paw.webapp.form;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class ModArtistForm {

    private long id;
    @Size(min = 2, max = 50)
    private String name;
    @Size(min = 2, max = 2048)
    private String bio;
    private MultipartFile artistImage;

    private List<ModAlbumForm> albums = new ArrayList<>();
    private boolean deleted;

    public ModArtistForm() {

    }

    public ModArtistForm(long id, String name, String bio, MultipartFile artistImage, List<ModAlbumForm> albums, boolean deleted) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.artistImage = artistImage;
        this.albums = albums;
        this.deleted = deleted;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public MultipartFile getArtistImage() {
        return artistImage;
    }

    public void setArtistImage(MultipartFile artistImage) {
        this.artistImage = artistImage;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public List<ModAlbumForm> getAlbums() {
        return albums;
    }

    public void setAlbum(List<ModAlbumForm> albums) {
        this.albums = albums;
    }


}
