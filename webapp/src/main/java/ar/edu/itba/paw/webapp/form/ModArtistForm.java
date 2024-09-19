package ar.edu.itba.paw.webapp.form;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;

public class ModArtistForm {

    @Size(min = 2, max = 50)
    private String name;
    @Size(min = 2, max = 400)
    private String bio;

    private MultipartFile file;

    public ModArtistForm(String name, String bio, MultipartFile file) {
        this.name = name;
        this.bio = bio;
        this.file = file;
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

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
