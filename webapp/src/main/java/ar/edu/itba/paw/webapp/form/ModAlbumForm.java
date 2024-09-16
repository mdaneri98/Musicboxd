package ar.edu.itba.paw.webapp.form;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;

public class ModAlbumForm {

    @Size(min = 1, max = 255)
    private String title;
    @Size(min = 1, max = 50)
    private String genre;
    private MultipartFile file;

    public ModAlbumForm(String title, String genre, MultipartFile file) {
        this.title = title;
        this.genre = genre;
        this.file = file;
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

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
