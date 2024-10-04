package ar.edu.itba.paw.models.dtos;

import java.util.ArrayList;
import java.util.List;

public class ArtistDTO {
    private long id;
    private String name;
    private String bio;
    private long imgId;
    private byte[] Image;
    private List<AlbumDTO> albums = new ArrayList<>();
    private boolean deleted;

    public ArtistDTO() {

    }

    public ArtistDTO(long id, String name, String bio, long imgId, byte[] Image, List<AlbumDTO> albums, boolean deleted) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.imgId = imgId;
        this.Image = Image;
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

    public long getImgId() {
        return imgId;
    }

    public void setImgId(long imgId) {
        this.imgId = imgId;
    }

    public byte[] getImage() {
        return Image;
    }

    public void setImage(byte[] Image) {
        this.Image = Image;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public List<AlbumDTO> getAlbums() {
        return albums;
    }

    public void setAlbum(List<AlbumDTO> albums) {
        this.albums = albums;
    }
}
