package ar.edu.itba.paw.webapp.form;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class ModAlbumForm {

    @Size(min = 1, max = 255)
    private String title;
    @Size(min = 1, max = 50)
    private String genre;
    private MultipartFile albumImage;

    private List<ModSongForm> songs = new ArrayList<>();

    public ModAlbumForm() {}

    public ModAlbumForm(String title, String genre, MultipartFile albumImage, List<ModSongForm> songs) {
        this.title = title;
        this.genre = genre;
        this.albumImage = albumImage;
        this.songs = songs;
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

    public MultipartFile getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(MultipartFile albumImage) {
        this.albumImage = albumImage;
    }

    public List<ModSongForm> getSongs() {
        return songs;
    }

    public void setSongs(List<ModSongForm> songs) {
        this.songs = songs;
    }
}
