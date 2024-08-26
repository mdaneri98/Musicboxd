package ar.edu.itba.paw;


import java.time.LocalDate;

public class Artist {
    private Long id;
    private String name;
    private String bio;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String img_src;

    public Artist(Long id, String name, String bio, LocalDate createdAt, LocalDate updatedAt, String img_src) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.img_src = img_src;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getImg_src() {
        return img_src;
    }

    public void setImg_src(String img_src) {
        this.img_src = img_src;
    }
}
