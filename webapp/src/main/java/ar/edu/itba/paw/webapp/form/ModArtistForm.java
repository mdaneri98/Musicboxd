package ar.edu.itba.paw.webapp.form;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class ModArtistForm {

    @Size(min = 2, max = 50)
    private String name;
    @Size(min = 2, max = 2048)
    private String bio;
    private MultipartFile artistImage;

    private List<ModAlbumForm> albums = new ArrayList<>();

    public ModArtistForm() {

    }

    public ModArtistForm(String name, String bio, MultipartFile artistImage, List<ModAlbumForm> albums) {
        this.name = name;
        this.bio = bio;
        this.artistImage = artistImage;
        this.albums = albums;
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

    public List<ModAlbumForm> getAlbums() {
        return albums;
    }

    public void setAlbum(List<ModAlbumForm> albums) {
        this.albums = albums;
    }


}
