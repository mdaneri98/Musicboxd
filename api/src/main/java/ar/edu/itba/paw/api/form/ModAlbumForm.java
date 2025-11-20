package ar.edu.itba.paw.api.form;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ModAlbumForm {

    @Size(min = 2, max = 100, message = "{validation.album.title.size}")
    private String title;

    @Size(min = 1, max = 50, message = "{validation.album.genre.size}")
    private String genre;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    // Hidden inputs
    private Long id;
    private Long albumImageId;
    private Long artistId;

    @Valid
    private List<ModSongForm> songs = new ArrayList<>();
    private boolean deleted = false;

    public ModAlbumForm() {}

    public ModAlbumForm(Long id, String title, String genre, LocalDate releaseDate, Long albumImageId, Long artistId, List<ModSongForm> songs, boolean deleted) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.albumImageId = albumImageId;
        this.artistId = artistId;
        this.songs = songs;
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getAlbumImageId() { return albumImageId; }

    public void setAlbumImageId(Long albumImageId) { this.albumImageId = albumImageId; }

    public Long getArtistId() { return artistId; }

    public void setArtistId(Long artistId) { this.artistId = artistId; }

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
