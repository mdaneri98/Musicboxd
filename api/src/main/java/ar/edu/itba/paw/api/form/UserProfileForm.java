package ar.edu.itba.paw.api.form;

import javax.validation.constraints.Size;

public class UserProfileForm {

    private String username;

    @Size(max = 100, message = "{validation.profile.name.size}")
    private String name;

    @Size(max = 255, message = "{validation.profile.bio.size}")
    private String bio;

    private Long imageId;

    public UserProfileForm() {}

    public UserProfileForm(String username, String name, String bio, Long imageId) {
        this.username = username;
        this.name = name;
        this.bio = bio;
        this.imageId = imageId;
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
