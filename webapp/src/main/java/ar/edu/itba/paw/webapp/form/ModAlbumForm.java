package ar.edu.itba.paw.webapp.form;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ModAlbumForm {

    @Size(min = 2, max = 255, message = "The title must be between 2 and 255 characters long")
    private String title;
    @Size(min = 1, max = 50, message = "The genre must be between 1 and 50 characters long")
    private String genre;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @Nullable
    private MultipartFile albumImage;

    // Hidden inputs
    private long id;
    private long albumImageId;
    private long artistId;

    @Valid
    private List<ModSongForm> songs = new ArrayList<>();
    private boolean deleted = false;

    public ModAlbumForm() {}

    public ModAlbumForm(long id, String title, String genre, LocalDate releaseDate, long albumImageId, MultipartFile albumImage, long artistId, List<ModSongForm> songs, boolean deleted) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.albumImageId = albumImageId;
        this.albumImage = albumImage;
        this.artistId = artistId;
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

    public long getAlbumImageId() { return albumImageId; }

    public void setAlbumImageId(long albumImageId) { this.albumImageId = albumImageId; }

    public MultipartFile getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(MultipartFile albumImage) {
        this.albumImage = albumImage;
    }

    public long getArtistId() { return artistId; }

    public void setArtistId(long artistId) { this.artistId = artistId; }

    public List<ModSongForm> getSongs() {
        return songs;
    }

    public void setSongs(List<ModSongForm> songs) {
        this.songs = songs;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
