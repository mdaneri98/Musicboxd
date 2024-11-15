package ar.edu.itba.paw.webapp.form;

import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import ar.edu.itba.paw.webapp.form.validation.UsernameNotInUse;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserProfileForm {


    @Size(min = 4, max = 50, message = "{validation.profile.username.size}")
    @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9]*", message = "{validation.profile.username.pattern}")
    private String username;

    @Size(max = 100, message = "{validation.profile.name.size}")
    private String name;

    @Size(max = 255, message = "{validation.profile.bio.size}")
    private String bio;

    @Nullable
    private final MultipartFile profilePicture;

    public UserProfileForm(String username, String name, String bio, MultipartFile profilePicture) {
        this.username = username;
        this.name = name;
        this.bio = bio;
        this.profilePicture = profilePicture;
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

    public MultipartFile getProfilePicture() {
        return profilePicture;
    }
}
