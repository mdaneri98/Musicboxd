package ar.edu.itba.paw.models.dtos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AlbumDTO {

    private long id;
    private String title;
    private String genre;
    private LocalDate releaseDate;
    private long imgId;
    private byte[] Image;

    private List<SongDTO> songs = new ArrayList<>();

    private boolean deleted = false;

    public AlbumDTO() {}

    public AlbumDTO(long id, String title, String genre, LocalDate releaseDate, long imgId, byte[] Image, List<SongDTO> songs, boolean deleted) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.imgId = imgId;
        this.Image = Image;
        this.songs = songs;
        this.deleted = deleted;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public LocalDate getReleaseDate() { return releaseDate; }

    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

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

    public List<SongDTO> getSongs() {
        return songs;
    }

    public void setSongs(List<SongDTO> songs) {
        this.songs = songs;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
