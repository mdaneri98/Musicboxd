package ar.edu.itba.paw.api.form;

import ar.edu.itba.paw.api.form.validation.UsernameNotInUse;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserProfileForm {

    @Size(min = 4, max = 50, message = "{validation.profile.username.size}")
    @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9]*", message = "{validation.profile.username.pattern}")
    @UsernameNotInUse(message = "{validation.profile.username.in.use}")
    private String username;

    @Size(max = 100, message = "{validation.profile.name.size}")
    private String name;

    @Size(max = 255, message = "{validation.profile.bio.size}")
    private String bio;

    private Long imageId;

    public UserProfileForm() {}

    public UserProfileForm(String username, String name, String bio) {
        this.username = username;
        this.name = name;
        this.bio = bio;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }
}
