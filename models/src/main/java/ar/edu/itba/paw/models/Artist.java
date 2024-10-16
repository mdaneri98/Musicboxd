package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "artist")
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "artist_id_seq")
    @SequenceGenerator(sequenceName = "artist_id_seq", name = "artist_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column
    private String bio;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Column(name = "img_id", nullable = false)
    private Long imgId;

    @Column(name = "rating_amount", nullable = false)
    private Integer ratingCount;

    @Column(name = "avg_rating", nullable = false)
    private Float avgRating;

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Album> albums = new ArrayList<>();

    public Artist() {
        // Constructor vacío necesario para JPA
    }

    public Artist(Long id, String name, String bio, LocalDate createdAt, LocalDate updatedAt, Long imgId, Integer ratingCount, Float avgRating) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.imgId = imgId;
        this.ratingCount = ratingCount;
        this.avgRating = avgRating;
    }

    public Artist(Long id, String name, String bio, Long imgId, Integer ratingCount, Float avgRating) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.imgId = imgId;
        this.ratingCount = ratingCount;
        this.avgRating = avgRating;
    }

    public Artist(Long id, String name, String bio, Long imgId) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.imgId = imgId;
    }

    public Artist(Long id, String name, Long imgId) {
        this.id = id;
        this.name = name;
        this.imgId = imgId;
    }

    public Artist(String name, String bio, Long imgId) {
        this.name = name;
        this.bio = bio;
        this.imgId = imgId;
    }

    public Artist(Long id) {
        this.id = id;
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

    public Long getImgId() {
        return imgId;
    }

    public void setImgId(Long imgId) {
        this.imgId = imgId;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Float avgRating) {
        this.avgRating = avgRating;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }
    
    // Método para convertir a JSON
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"type\":\"").append("artist").append("\",");
        json.append("\"id\":").append(id).append(",");
        json.append("\"name\":\"").append(name).append("\",");
        json.append("\"bio\":\"").append(bio).append("\",");
        json.append("\"createdAt\":\"").append(createdAt != null ? createdAt.toString() : null).append("\",");
        json.append("\"updatedAt\":\"").append(updatedAt != null ? updatedAt.toString() : null).append("\",");
        json.append("\"imgId\":").append(imgId);
        json.append("}");
        return json.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artist artist = (Artist) o;
        if (!Objects.equals(id, artist.id)) return false;
        if (!Objects.equals(name, artist.name)) return false;
        if (!Objects.equals(bio, artist.bio)) return false;
        return Objects.equals(imgId, artist.imgId);
    }
}
