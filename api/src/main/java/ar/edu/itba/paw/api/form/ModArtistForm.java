package ar.edu.itba.paw.api.form;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class ModArtistForm {

    @Size(min = 2, max = 50, message = "{validation.artist.name.size}")
    private String name;

    @Size(min = 2, max = 2048, message = "{validation.artist.bio.size}")
    private String bio;

    @Valid
    private List<ModAlbumForm> albums = new ArrayList<>();

    // Hidden inputs
    private Long id;
    private Long artistImgId;
    private boolean deleted;

    public ModArtistForm() {

    }

    public ModArtistForm(Long id, String name, String bio, Long artistImgId, List<ModAlbumForm> albums, boolean deleted) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.artistImgId = artistImgId;
        this.albums = albums;
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getArtistImgId() {
        return artistImgId;
    }

    public void setArtistImgId(Long artistImgId) {
        this.artistImgId = artistImgId;
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

    public void setAlbums(List<ModAlbumForm> albums) {
        this.albums = albums;
    }


}
